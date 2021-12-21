package com.hongcha.remote.core;

import com.hongcha.remote.common.LifeCycle;
import com.hongcha.remote.common.MessageFuture;
import com.hongcha.remote.common.Pair;
import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.common.constant.RemoteConstant;
import com.hongcha.remote.common.exception.RemoteException;
import com.hongcha.remote.common.exception.RemoteExceptionBody;
import com.hongcha.remote.common.process.RequestProcess;
import com.hongcha.remote.common.util.Assert;
import com.hongcha.remote.core.bootstrap.AbstractBootStrap;
import com.hongcha.remote.core.config.RemoteConfig;
import com.hongcha.remote.core.generator.AtomicIntegerIDGenerator;
import com.hongcha.remote.core.generator.IDGenerator;
import com.hongcha.remote.core.util.RemoteUtils;
import com.hongcha.remote.filter.consumer.DefaultRequestFilterChin;
import com.hongcha.remote.filter.consumer.RequestFilterChain;
import com.hongcha.remote.filter.provider.DefaultResponseFilterChain;
import com.hongcha.remote.filter.provider.ResponseFilterChain;
import com.hongcha.remote.protocol.Protocol;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;


public abstract class AbstractRemote<T extends AbstractBootStrap> implements LifeCycle {
    protected final Map<Integer, Pair<RequestProcess, EventLoopGroup>> codeProcessMap = new HashMap<>();

    protected final Map<Integer, MessageFuture> futureHashMap = new HashMap<>();

    private final RemoteConfig config;

    protected EventLoopGroup defaultEventLoopGroup = new NioEventLoopGroup(1);

    protected IDGenerator IDGenerator = new AtomicIntegerIDGenerator();

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
        defaultEventLoopGroup.shutdownGracefully();
    }

    protected abstract T getBootStrap();


    public RemoteConfig getConfig() {
        return config;
    }

    public void registerProcess(int code, RequestProcess requestProcess, EventLoopGroup eventLoopGroup) {
        codeProcessMap.put(code, new Pair(requestProcess, eventLoopGroup));
    }


    public MessageFuture createMessageFuture(RequestCommon requestCommon) {
        requestCommon.setId(IDGenerator.nextId());
        int id = requestCommon.getId();
        MessageFuture messageFuture = new MessageFuture(requestCommon);
        futureHashMap.put(id, messageFuture);
        return messageFuture;
    }


    protected RequestCommon send(Channel channel, RequestCommon requestCommon) throws ExecutionException, InterruptedException {
        MessageFuture messageFuture = asyncSend(channel, requestCommon);
        return messageFuture.getFuture().get();
    }

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
        Pair<RequestProcess, EventLoopGroup> requestProcessEventLoopGroupPair = codeProcessMap.get(req.getCode());
        if (requestProcessEventLoopGroupPair != null) {
            RequestProcess requestProcess = requestProcessEventLoopGroupPair.getKey();
            EventLoopGroup eventLoopGroup = requestProcessEventLoopGroupPair.getValue();
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
                    future.completeExceptionally(new RemoteException(RemoteUtils.getBody(msg, RemoteExceptionBody.class)));
                }
            }
        }

    }


    /**
     * 根据旧的oldRequestCommon构造新的oldRequestCommon,不会设置#{@link RequestCommon#setDirection(byte)}
     */
    protected RequestCommon buildRequestCommon(RequestCommon oldRequestCommon, Object body) {
        RequestCommon requestCommon = new RequestCommon();
        requestCommon.setId(oldRequestCommon.getId());
        requestCommon.setCode(oldRequestCommon.getCode());
        requestCommon.setProtocol(oldRequestCommon.getProtocol());
        requestCommon.setHeaderLength(oldRequestCommon.getHeaderLength());
        requestCommon.setHeaders(oldRequestCommon.getHeaders());
        Protocol protocol = RemoteUtils.getProtocolFactory().getValue(requestCommon.getProtocol());
        Assert.notNull(protocol, "protocol code " + requestCommon.getProtocol() + " not found");
        byte[] encode = protocol.encode(body);
        requestCommon.setBody(encode);
        return requestCommon;
    }

    protected RequestFilterChain getRequestFilter(Supplier<MessageFuture> messageFutureSupplier) {
        return new DefaultRequestFilterChin(RemoteUtils.getFilterConsumerFactory().getValues(), messageFutureSupplier);
    }

    protected ResponseFilterChain getResponseFilterChain(ChannelHandlerContext ctx, RequestProcess requestProcess) {
        return new DefaultResponseFilterChain(RemoteUtils.getFilterProviderFactory().getValues(), ctx, requestProcess);
    }

    protected abstract class AbstractHandler extends SimpleChannelInboundHandler<RequestCommon> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RequestCommon msg) throws Exception {
            doProcess(ctx, msg);
        }
    }

}
