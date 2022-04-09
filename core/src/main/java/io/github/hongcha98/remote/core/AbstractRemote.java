package io.github.hongcha98.remote.core;

import io.github.hongcha98.remote.common.LifeCycle;
import io.github.hongcha98.remote.common.Message;
import io.github.hongcha98.remote.common.MessageFuture;
import io.github.hongcha98.remote.common.Pair;
import io.github.hongcha98.remote.common.constant.RemoteConstant;
import io.github.hongcha98.remote.common.exception.RemoteException;
import io.github.hongcha98.remote.common.exception.RemoteExceptionBody;
import io.github.hongcha98.remote.common.process.Process;
import io.github.hongcha98.remote.common.spi.SpiLoader;
import io.github.hongcha98.remote.core.bootstrap.AbstractBootStrap;
import io.github.hongcha98.remote.core.config.RemoteConfig;
import io.github.hongcha98.remote.core.generator.IDGenerator;
import io.github.hongcha98.remote.core.util.ProtocolUtils;
import io.github.hongcha98.remote.protocol.Protocol;
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

    protected final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    private final RemoteConfig config;

    protected AbstractRemote(RemoteConfig config) {
        this.config = config;
    }

    @Override
    public void start() {
        getBootStrap().start();
        scheduledExecutorService.scheduleAtFixedRate(() -> futureTimeOut(), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    protected void futureTimeOut() {
        futureMap.forEach((id, message) -> {
            if (message.isTimeOut()) {
                message.getRespFuture().completeExceptionally(new TimeoutException("time limit exceeded"));
                futureMap.remove(id);
            }
        });
    }

    @Override
    public void close() {
        scheduledExecutorService.shutdown();
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
        return buildRequest(msg, code, RemoteConstant.DEFAULT_PROTOCOL);
    }

    public Message buildRequest(Object msg, int code, byte protocol) {
        Message request = new Message();
        request.setId(getIDGenerator().nextId());
        request.setCode(code);
        request.setProtocol(protocol);
        request.setBody(SpiLoader.load(Protocol.class, protocol).encode(msg));
        request.setDirection(false);
        return request;
    }

    public Message buildResponse(Message request, Object msg) {
        return buildResponse(request, msg, request.getCode());
    }

    public Message buildError(Message message, Throwable throwable) {
        return buildResponse(message, new RemoteExceptionBody(throwable), RemoteConstant.ERROR_CODE);
    }

    public Message buildResponse(Message request, Object msg, int code) {
        Message response = new Message();
        response.setId(request.getId());
        response.setCode(code);
        response.setProtocol(request.getProtocol());
        response.setBody(SpiLoader.load(Protocol.class, request.getProtocol()).encode(msg));
        response.setDirection(true);
        return response;
    }

    protected abstract IDGenerator getIDGenerator();

    public <T> T send(Channel channel, Message message, Class<T> clazz) {
        return send(channel, message, clazz, getConfig().getTimeOut(), TimeUnit.MILLISECONDS);
    }

    /**
     * 同步发送，并等待响应
     */
    public <T> T send(Channel channel, Message message, Class<T> clazz, long timeOut, TimeUnit timeUnit) {
        try {
            return ProtocolUtils.decode(asyncSend(channel, message).getRespFuture().get(timeOut, timeUnit), clazz);
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }

    public MessageFuture asyncSend(Channel channel, Message message) {
        MessageFuture messageFuture = new MessageFuture(message, getConfig().getTimeOut(), TimeUnit.MILLISECONDS);
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
        processResp(ctx, req);
        Pair<Process, ExecutorService> requestProcessEventLoopGroupPair = codeProcessMap.get(req.getCode());
        if (requestProcessEventLoopGroupPair != null) {
            Process process = requestProcessEventLoopGroupPair.getKey();
            ExecutorService eventLoopGroup = requestProcessEventLoopGroupPair.getValue();
            eventLoopGroup.execute(() -> {
                process.process(ctx, req);
            });
        }
    }

    protected void processResp(ChannelHandlerContext ctx, Message msg) {
        if (msg.isResponse()) {
            MessageFuture messageFuture = futureMap.remove(msg.getId());
            if (messageFuture != null) {
                boolean isSuccess = msg.getCode() != RemoteConstant.ERROR_CODE;
                CompletableFuture<Message> future = messageFuture.getRespFuture();
                if (isSuccess) {
                    future.complete(msg);
                } else {
                    future.completeExceptionally(new RemoteException(SpiLoader.load(Protocol.class, msg.getProtocol()).decode(msg.getBody(), RemoteExceptionBody.class)));
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
