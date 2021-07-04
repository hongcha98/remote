package com.hongcha.remoting.core;

import com.hongcha.remoting.common.LifeCcycle;
import com.hongcha.remoting.common.MessageFuture;
import com.hongcha.remoting.common.Pair;
import com.hongcha.remoting.common.dto.RequestCommon;
import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.common.process.RequestProcess;
import com.hongcha.remoting.common.util.Assert;
import com.hongcha.remoting.core.bootstrap.AbstractBootStrap;
import com.hongcha.remoting.core.config.RemotingConfig;
import com.hongcha.remoting.core.process.ErrorRequestProcess;
import com.hongcha.remoting.filter.consumer.ConsumerFilterChain;
import com.hongcha.remoting.filter.consumer.DefaultConsumerFilterChin;
import com.hongcha.remoting.filter.provider.DefaultProviderFilterChain;
import com.hongcha.remoting.filter.provider.ProviderFilterChain;
import com.hongcha.remoting.protocol.Protocol;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public abstract class AbstractRemoting<T extends AbstractBootStrap> implements LifeCcycle {


    protected final Map<Integer, Pair<RequestProcess, EventLoopGroup>> CODE_PROCESS_MAP = new HashMap<>();

    protected final Map<Integer, MessageFuture> MESSAGE_FUTURE_MAP = new ConcurrentHashMap<>();

    private final RemotingConfig config;

    protected EventLoopGroup DEFAULT_EVENT_LOOP_GROUP = new NioEventLoopGroup(1);

    private AtomicInteger id = new AtomicInteger(1);


    protected AbstractRemoting(RemotingConfig config) {
        this.config = config;
    }


    @Override
    public void init() throws Exception {
        getBootStrap().init();
        registerDefaultProcess();
    }


    protected void registerDefaultProcess() {
        registerProcess(500, new ErrorRequestProcess(), DEFAULT_EVENT_LOOP_GROUP);
    }


    @Override
    public void start() throws Exception {
        getBootStrap().start();
    }


    @Override
    public void close() throws Exception {
        getBootStrap().close();
        DEFAULT_EVENT_LOOP_GROUP.shutdownGracefully();
    }

    protected abstract T getBootStrap();


    public RemotingConfig getConfig() {
        return config;
    }


    public MessageFuture createAndPutMessageFuture(RequestCommon requestCommon) {
        int id = requestCommon.getId();
        MessageFuture messageFuture = new MessageFuture(requestCommon);
        MESSAGE_FUTURE_MAP.put(id, messageFuture);
        return messageFuture;
    }

    public MessageFuture removeId(RequestCommon requestCommon) {
        return removeId(requestCommon.getId());
    }


    public MessageFuture removeId(int id) {
        return MESSAGE_FUTURE_MAP.remove(id);
    }


    public void registerProcess(int code, RequestProcess requestProcess, EventLoopGroup eventLoopGroup) {
        CODE_PROCESS_MAP.put(code, new Pair(requestProcess, eventLoopGroup));
    }

    protected RequestCommon send(Channel channel, RequestMessage requestMessage) throws ExecutionException, InterruptedException {
        return send(channel, buildRequestCommon(requestMessage));
    }


    protected RequestCommon send(Channel channel, RequestCommon requestCommon) throws ExecutionException, InterruptedException {
        MessageFuture messageFuture = asyncSend(channel, requestCommon);
        return messageFuture.getFuture().get();
    }

    protected MessageFuture asyncSend(Channel channel, RequestMessage requestMessage) throws ExecutionException, InterruptedException {
        return asyncSend(channel, buildRequestCommon(requestMessage));
    }

    protected MessageFuture asyncSend(Channel channel, RequestCommon requestCommon) throws ExecutionException, InterruptedException {
        return getConsumerFilterChin(() -> {
            MessageFuture messageFuture = createAndPutMessageFuture(requestCommon);

            ChannelFuture channelFuture = channel.writeAndFlush(requestCommon);

            channelFuture.addListener((future -> {
                if (!future.isSuccess()) {
                    messageFuture.getFuture().completeExceptionally(channelFuture.cause());
                }
            }));

            return messageFuture;
        }).process(requestCommon);
    }

    protected void doProcess(ChannelHandlerContext ctx, RequestCommon msg) {
        preProcessing(ctx, msg);
        Pair<RequestProcess, EventLoopGroup> requestProcessEventLoopGroupPair = CODE_PROCESS_MAP.get(msg.getCode());
        if (requestProcessEventLoopGroupPair != null) {
            RequestProcess requestProcess = requestProcessEventLoopGroupPair.getKey();
            EventLoopGroup eventLoopGroup = requestProcessEventLoopGroupPair.getValue();
            eventLoopGroup.execute(() -> {
                RequestMessage req = buildRequestMessage(msg);
                ProviderFilterChain filterChin = getProviderFilterChin(requestProcess);
                RequestMessage resp;
                try {
                    resp = filterChin.process(req);
                    if (resp != null) {
                        resp.setDirection((byte) 1);
                        ctx.channel().writeAndFlush(buildRequestCommon(resp));
                    }
                } catch (Throwable e) {
                    RequestCommon requestCommon = buildRequestCommon(msg, e);
                    requestCommon.setDirection((byte) 1);
                    ctx.channel().writeAndFlush(requestCommon);
                }
            });
        } else {
            //TODO 输出未找到code对应的处理器
        }
    }

    protected void preProcessing(ChannelHandlerContext ctx, RequestCommon msg) {
        byte direction = msg.getDirection();

        if (direction == 1) {
            MessageFuture messageFuture = MESSAGE_FUTURE_MAP.remove(msg.getId());
            if (messageFuture != null) {
                boolean isSuccess = msg.getCode() != 500;
                CompletableFuture<RequestCommon> future = messageFuture.getFuture();
                if (isSuccess) {
                    future.complete(msg);
                } else {
                    future.completeExceptionally(RemotingFactory.getBody(msg));
                }
            }

        }

    }

    /**
     * 根据RequestMessage 构造 RequestCommon
     *
     * @param message
     * @return
     */
    protected RequestCommon buildRequestCommon(RequestMessage message) {
        RequestCommon requestCommon = new RequestCommon();
        requestCommon.setId(nextId());
        requestCommon.setCode(message.getCode());
        requestCommon.setProtocol(message.getProtocol());
        requestCommon.setDirection(message.getDirection());
        requestCommon.setHeaders(message.getHeaders());
        Protocol protocol = RemotingFactory.getProtocolFactory().getObject(message.getProtocol());
        Assert.notNull(protocol, "protocol code " + message.getProtocol() + " not found");
        byte[] encode = protocol.encode(message.getMsg());
        requestCommon.setBody(encode);
        return requestCommon;
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
        Protocol protocol = RemotingFactory.getProtocolFactory().getObject(requestCommon.getProtocol());
        Assert.notNull(protocol, "protocol code " + requestCommon.getProtocol() + " not found");
        byte[] encode = protocol.encode(body);
        requestCommon.setBody(encode);
        return requestCommon;
    }


    /**
     * 根据RequestCommon构建RequestMessage
     *
     * @param requestCommon
     * @return
     */
    protected RequestMessage buildRequestMessage(RequestCommon requestCommon) {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setCode(requestCommon.getCode());
        requestMessage.setProtocol(requestCommon.getProtocol());
        requestMessage.setDirection(requestCommon.getDirection());
        requestMessage.setHeaders(requestCommon.getHeaders());
        Object msg = RemotingFactory.getBody(requestCommon);
        requestMessage.setMsg(msg);
        return requestMessage;
    }

    /**
     * 根据旧的RequestMessage 构造新的RequestMessage 不会设置{@link RequestMessage#setMsg(Object)}
     *
     * @param oldRequestMessage
     * @return
     */
    protected RequestMessage buildRequestMessage(RequestMessage oldRequestMessage) {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setCode(oldRequestMessage.getCode());
        requestMessage.setProtocol(oldRequestMessage.getProtocol());
        requestMessage.setDirection(oldRequestMessage.getDirection());
        requestMessage.setHeaders(oldRequestMessage.getHeaders());
        return requestMessage;
    }

    /**
     * 获取下一个id序号
     *
     * @return
     */
    protected int nextId() {
        return id.getAndIncrement();
    }

    protected ConsumerFilterChain getConsumerFilterChin(Supplier<MessageFuture> messageFutureSupplier) {
        return new DefaultConsumerFilterChin(RemotingFactory.getFilterConsumerFactory().getAll(), messageFutureSupplier);
    }

    protected ProviderFilterChain getProviderFilterChin(RequestProcess requestProcess) {
        return new DefaultProviderFilterChain(RemotingFactory.getFilterProviderFactory().getAll(), requestProcess);
    }

    protected abstract class AbstractHandler extends SimpleChannelInboundHandler<RequestCommon> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RequestCommon msg) throws Exception {
            doProcess(ctx, msg);
        }
    }

}
