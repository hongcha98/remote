package com.hongcha.remote.core;


import com.hongcha.remote.common.Message;
import com.hongcha.remote.core.bootstrap.RemoteClientBootStrap;
import com.hongcha.remote.core.config.RemoteConfig;
import com.hongcha.remote.core.generator.AtomicIntegerIDGenerator;
import com.hongcha.remote.core.generator.IDGenerator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class RemoteClient extends AbstractRemote<RemoteClientBootStrap> {

    private RemoteClientBootStrap remoteClientBootStrap;

    private IDGenerator idGenerator = new AtomicIntegerIDGenerator();

    private ChannelHandler channelHandler = new ClientHandler();

    public RemoteClient(RemoteConfig config) {
        super(config);
    }


    @Override
    public RemoteClientBootStrap getBootStrap() {
        return remoteClientBootStrap;
    }

    @Override
    public void start() throws Exception {
        remoteClientBootStrap = new RemoteClientBootStrap(getConfig()) {
            @Override
            public ChannelHandler[] getHandlerArray() {
                return new ChannelHandler[]{channelHandler};
            }
        };
        super.start();
    }

    @Override
    protected IDGenerator getIDGenerator() {
        return idGenerator;
    }

    public <T> T send(String host, int port, Message message, Class<T> clazz) throws InterruptedException, ExecutionException, TimeoutException {
        return send(getChannel(host, port), message, clazz);
    }

    public <T> T send(String host, int port, Message message, Class<T> clazz, long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return send(getChannel(host, port), message, clazz, timeout, timeUnit);
    }

    public <T> T send(SocketAddress socketAddress, Message message, Class<T> clazz) throws InterruptedException, ExecutionException, TimeoutException {
        return send(getChannel(socketAddress), message, clazz);
    }

    public <T> T send(SocketAddress socketAddress, Message message, Class<T> clazz, long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return send(getChannel(socketAddress), message, clazz, timeout, timeUnit);
    }

    public CompletableFuture<Message> send(String host, int port, Message message) throws InterruptedException, ExecutionException {
        return asyncSend(getChannel(host, port), message).getRespFuture();
    }

    public CompletableFuture<Message> send(SocketAddress socketAddress, Message message) throws InterruptedException, ExecutionException {
        return asyncSend(getChannel(socketAddress), message).getRespFuture();
    }

    private Channel getChannel(String host, int port) throws InterruptedException {
        return remoteClientBootStrap.connect(host, port);
    }

    private Channel getChannel(SocketAddress socketAddress) throws InterruptedException {
        return remoteClientBootStrap.connect(socketAddress);
    }


    public class ClientHandler extends AbstractHandler {

    }

}
