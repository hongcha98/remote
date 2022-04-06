package com.hongcha.remote.core;

import com.hongcha.remote.common.LifeCycle;
import com.hongcha.remote.common.Message;
import com.hongcha.remote.common.MessageFuture;
import com.hongcha.remote.common.Pair;
import com.hongcha.remote.common.constant.RemoteConstant;
import com.hongcha.remote.common.exception.RemoteException;
import com.hongcha.remote.common.exception.RemoteExceptionBody;
import com.hongcha.remote.common.process.Process;
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
    protected final Map<Integer, Pair<Process, ExecutorService>> codeProcessMap = new HashMap<>();

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

    public void registerProcess(int code, Process process, ExecutorService executorService) {
        codeProcessMap.put(code, new Pair(process, executorService));
    }


    public Message buildRequest(Object msg, int code) {
        Message request = new Message();
        request.setId(getIDGenerator().nextId());
        request.setCode(code);
        request.setProtocol((byte) 1);
        request.setBody(SpiLoader.load(Protocol.class, request.getProtocol()).encode(msg));
        request.setDirection(false);
        return request;
    }

    public Message buildResponse(Message request, Object msg) {
        return buildResponse(request, msg, request.getCode());
    }

    public Message buildResponse(Message request, Object msg, int code) {
        Message response = new Message();
        response.setId(request.getId());
        response.setCode(code);
        response.setProtocol(request.getProtocol());
        response.setBody(SpiLoader.load(Protocol.class, response.getProtocol()).encode(msg));
        response.setDirection(true);
        return response;
    }

    protected abstract IDGenerator getIDGenerator();

    public <T> T send(Channel channel, Message message, Class<T> clazz) throws ExecutionException, InterruptedException, TimeoutException {
        return send(channel, message, clazz, getConfig().getTimeOut(), TimeUnit.MILLISECONDS);
    }

    /**
     * 同步发送，并等待响应
     *
     * @param channel
     * @param message
     * @param clazz
     * @param timeOut
     * @param timeUnit
     * @param <T>
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public <T> T send(Channel channel, Message message, Class<T> clazz, long timeOut, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        return ProtocolUtils.decode(asyncSend(channel, message).getRespFuture().get(timeOut, timeUnit), clazz);
    }

    public MessageFuture asyncSend(Channel channel, Message message) throws ExecutionException, InterruptedException {
        MessageFuture messageFuture = new MessageFuture(message);
        futureMap.put(message.getId(), messageFuture);
        ChannelFuture channelFuture = channel.writeAndFlush(message);
        channelFuture.addListener((future -> {
            if (!future.isSuccess()) {
                futureMap.remove(message.getId());
                messageFuture.getRespFuture().completeExceptionally(channelFuture.cause());
            }
        }));
        return messageFuture;
    }

    protected void doProcess(ChannelHandlerContext ctx, Message req) {
        preProcess(ctx, req);
        Pair<Process, ExecutorService> requestProcessEventLoopGroupPair = codeProcessMap.get(req.getCode());
        if (requestProcessEventLoopGroupPair != null) {
            Process process = requestProcessEventLoopGroupPair.getKey();
            ExecutorService eventLoopGroup = requestProcessEventLoopGroupPair.getValue();
            eventLoopGroup.execute(() -> {
                process.process(ctx, req);
            });
        }
    }

    protected void preProcess(ChannelHandlerContext ctx, Message msg) {
        if (msg.isResponse()) {
            MessageFuture messageFuture = futureMap.remove(msg.getId());
            if (messageFuture != null) {
                boolean isSuccess = msg.getCode() != RemoteConstant.ERROR_CODE;
                CompletableFuture<Message> future = messageFuture.getRespFuture();
                if (isSuccess) {
                    future.complete(msg);
                } else {
                    future.completeExceptionally(new RemoteException(SpiLoader.load(Protocol.class, msg.getCode()).decode(msg.getBody(), RemoteExceptionBody.class)));
                }
            }
        }

    }


    protected abstract class AbstractHandler extends SimpleChannelInboundHandler<Message> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
            doProcess(ctx, msg);
        }
    }

}
