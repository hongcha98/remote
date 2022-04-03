package com.hongcha.remote.core;

import com.hongcha.remote.common.LifeCycle;
import com.hongcha.remote.common.MessageFuture;
import com.hongcha.remote.common.Pair;
import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.common.constant.RemoteConstant;
import com.hongcha.remote.common.exception.RemoteException;
import com.hongcha.remote.common.exception.RemoteExceptionBody;
import com.hongcha.remote.common.process.RequestProcess;
import com.hongcha.remote.common.spi.SpiLoader;
import com.hongcha.remote.core.bootstrap.AbstractBootStrap;
import com.hongcha.remote.core.config.RemoteConfig;
import com.hongcha.remote.core.generator.IDGenerator;
import com.hongcha.remote.filter.consumer.DefaultRequestFilterChin;
import com.hongcha.remote.filter.consumer.RequestFilter;
import com.hongcha.remote.filter.consumer.RequestFilterChain;
import com.hongcha.remote.filter.provider.DefaultResponseFilterChain;
import com.hongcha.remote.filter.provider.ResponseFilter;
import com.hongcha.remote.filter.provider.ResponseFilterChain;
import com.hongcha.remote.protocol.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;


public abstract class AbstractRemote<T extends AbstractBootStrap> implements LifeCycle {
    protected final Map<Integer, Pair<RequestProcess, ExecutorService>> codeProcessMap = new HashMap<>();

    protected final Map<Integer, MessageFuture> futureHashMap = new HashMap<>();

    private final RemoteConfig config;

    protected AbstractRemote(RemoteConfig config) {
        this.config = config;
    }

    @Override
    public void start() throws Exception {
        getBootStrap().start();
    }

    @Override
    public void close() throws Exception {
        getBootStrap().close();
    }

    protected abstract T getBootStrap();


    public RemoteConfig getConfig() {
        return config;
    }

    public void registerProcess(int code, RequestProcess requestProcess, ExecutorService executorService) {
        codeProcessMap.put(code, new Pair(requestProcess, executorService));
    }


    public MessageFuture createMessageFuture(RequestCommon requestCommon) {
        requestCommon.setId(getIDGenerator().nextId());
        int id = requestCommon.getId();
        MessageFuture messageFuture = new MessageFuture(requestCommon);
        futureHashMap.put(id, messageFuture);
        return messageFuture;
    }

    protected abstract IDGenerator getIDGenerator();


    protected MessageFuture asyncSend(Channel channel, RequestCommon requestCommon) throws ExecutionException, InterruptedException {
        return getRequestFilter(() -> {
            MessageFuture messageFuture = createMessageFuture(requestCommon);
            ChannelFuture channelFuture = channel.writeAndFlush(requestCommon);
            channelFuture.addListener((future -> {
                if (!future.isSuccess()) {
                    futureHashMap.remove(requestCommon.getId());
                    messageFuture.getFuture().completeExceptionally(channelFuture.cause());
                }
            }));
            return messageFuture;
        }).process(requestCommon);
    }

    protected void doProcess(ChannelHandlerContext ctx, RequestCommon req) {
        preProcess(ctx, req);
        Pair<RequestProcess, ExecutorService> requestProcessEventLoopGroupPair = codeProcessMap.get(req.getCode());
        if (requestProcessEventLoopGroupPair != null) {
            RequestProcess requestProcess = requestProcessEventLoopGroupPair.getKey();
            ExecutorService eventLoopGroup = requestProcessEventLoopGroupPair.getValue();
            eventLoopGroup.execute(() -> {
                getResponseFilterChain(ctx, requestProcess).process(req);
            });
        }
    }

    protected void preProcess(ChannelHandlerContext ctx, RequestCommon msg) {
        if (msg.isResponse()) {
            MessageFuture messageFuture = futureHashMap.remove(msg.getId());
            if (messageFuture != null) {
                boolean isSuccess = msg.getCode() != RemoteConstant.ERROR_CODE;
                CompletableFuture<RequestCommon> future = messageFuture.getFuture();
                if (isSuccess) {
                    future.complete(msg);
                } else {
                    future.completeExceptionally(new RemoteException(SpiLoader.DEFAULT.load(Protocol.class, msg.getCode()).decode(msg.getBody(), RemoteExceptionBody.class)));
                }
            }
        }

    }


    protected RequestFilterChain getRequestFilter(Supplier<MessageFuture> messageFutureSupplier) {
        return new DefaultRequestFilterChin(SpiLoader.DEFAULT.loadAll(RequestFilter.class), messageFutureSupplier);
    }

    protected ResponseFilterChain getResponseFilterChain(ChannelHandlerContext ctx, RequestProcess requestProcess) {
        return new DefaultResponseFilterChain(SpiLoader.DEFAULT.loadAll(ResponseFilter.class), ctx, requestProcess);
    }

    protected abstract class AbstractHandler extends SimpleChannelInboundHandler<RequestCommon> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RequestCommon msg) throws Exception {
            doProcess(ctx, msg);
        }
    }

}
