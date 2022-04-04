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
import com.hongcha.remote.core.util.ProtocolUtils;
import com.hongcha.remote.protocol.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public abstract class AbstractRemote<T extends AbstractBootStrap> implements LifeCycle {
    protected final Map<Integer, Pair<RequestProcess, ExecutorService>> codeProcessMap = new HashMap<>();

    protected final Map<Integer, MessageFuture> futureMap = new ConcurrentHashMap<>();

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


    public RequestCommon buildRequest(Object msg, int code) {
        RequestCommon request = new RequestCommon();
        request.setId(getIDGenerator().nextId());
        request.setCode(code);
        request.setProtocol((byte) 1);
        request.setBody(SpiLoader.load(Protocol.class, request.getProtocol()).encode(msg));
        request.setDirection(false);
        return request;
    }

    public RequestCommon buildResponse(RequestCommon request, Object msg, int code) {
        RequestCommon response = new RequestCommon();
        response.setId(request.getId());
        response.setCode(code);
        response.setProtocol(request.getProtocol());
        response.setBody(SpiLoader.load(Protocol.class, response.getProtocol()).encode(msg));
        response.setDirection(true);
        return response;
    }

    protected abstract IDGenerator getIDGenerator();

    public <T> T send(Channel channel, RequestCommon requestCommon, Class<T> clazz) throws ExecutionException, InterruptedException, TimeoutException {
        return send(channel, requestCommon, clazz, getConfig().getTimeOut(), TimeUnit.MILLISECONDS);
    }

    /**
     * 同步发送，并等待响应
     *
     * @param channel
     * @param requestCommon
     * @param clazz
     * @param timeOut
     * @param timeUnit
     * @param <T>
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public <T> T send(Channel channel, RequestCommon requestCommon, Class<T> clazz, long timeOut, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        return ProtocolUtils.decode(asyncSend(channel, requestCommon).getFuture().get(timeOut, timeUnit), clazz);
    }

    public MessageFuture asyncSend(Channel channel, RequestCommon requestCommon) throws ExecutionException, InterruptedException {
        MessageFuture messageFuture = new MessageFuture(requestCommon);
        futureMap.put(requestCommon.getId(), messageFuture);
        ChannelFuture channelFuture = channel.writeAndFlush(requestCommon);
        channelFuture.addListener((future -> {
            if (!future.isSuccess()) {
                futureMap.remove(requestCommon.getId());
                messageFuture.getFuture().completeExceptionally(channelFuture.cause());
            }
        }));
        return messageFuture;
    }

    protected void doProcess(ChannelHandlerContext ctx, RequestCommon req) {
        preProcess(ctx, req);
        Pair<RequestProcess, ExecutorService> requestProcessEventLoopGroupPair = codeProcessMap.get(req.getCode());
        if (requestProcessEventLoopGroupPair != null) {
            RequestProcess requestProcess = requestProcessEventLoopGroupPair.getKey();
            ExecutorService eventLoopGroup = requestProcessEventLoopGroupPair.getValue();
            eventLoopGroup.execute(() -> {
                requestProcess.process(ctx, req);
            });
        }
    }

    protected void preProcess(ChannelHandlerContext ctx, RequestCommon msg) {
        if (msg.isResponse()) {
            MessageFuture messageFuture = futureMap.remove(msg.getId());
            if (messageFuture != null) {
                boolean isSuccess = msg.getCode() != RemoteConstant.ERROR_CODE;
                CompletableFuture<RequestCommon> future = messageFuture.getFuture();
                if (isSuccess) {
                    future.complete(msg);
                } else {
                    future.completeExceptionally(new RemoteException(SpiLoader.load(Protocol.class, msg.getCode()).decode(msg.getBody(), RemoteExceptionBody.class)));
                }
            }
        }

    }


    protected abstract class AbstractHandler extends SimpleChannelInboundHandler<RequestCommon> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RequestCommon msg) throws Exception {
            doProcess(ctx, msg);
        }
    }

}
