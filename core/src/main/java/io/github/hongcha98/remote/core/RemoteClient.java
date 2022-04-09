package io.github.hongcha98.remote.core;


import io.github.hongcha98.remote.common.Message;
import io.github.hongcha98.remote.core.bootstrap.RemoteClientBootStrap;
import io.github.hongcha98.remote.core.config.RemoteConfig;
import io.github.hongcha98.remote.core.generator.AtomicIntegerIDGenerator;
import io.github.hongcha98.remote.core.generator.IDGenerator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


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
    public void start() {
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

    public <T> T send(String host, int port, Message message, Class<T> clazz) {
        return send(getChannel(host, port), message, clazz);
    }

    public <T> T send(String host, int port, Message message, Class<T> clazz, long timeout, TimeUnit timeUnit) {
        return send(getChannel(host, port), message, clazz, timeout, timeUnit);
    }

    public <T> T send(SocketAddress socketAddress, Message message, Class<T> clazz) {
        return send(getChannel(socketAddress), message, clazz);
    }

    public <T> T send(SocketAddress socketAddress, Message message, Class<T> clazz, long timeout, TimeUnit timeUnit) {
        return send(getChannel(socketAddress), message, clazz, timeout, timeUnit);
    }

    public CompletableFuture<Message> send(String host, int port, Message message) {
        return asyncSend(getChannel(host, port), message).getRespFuture();
    }

    public CompletableFuture<Message> send(SocketAddress socketAddress, Message message) {
        return asyncSend(getChannel(socketAddress), message).getRespFuture();
    }

    private Channel getChannel(String host, int port) {
        return remoteClientBootStrap.connect(host, port);
    }

    private Channel getChannel(SocketAddress socketAddress) {
        return remoteClientBootStrap.connect(socketAddress);
    }

    @ChannelHandler.Sharable
    public class ClientHandler extends AbstractHandler {

    }

}
