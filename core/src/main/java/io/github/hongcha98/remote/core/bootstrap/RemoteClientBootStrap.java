package io.github.hongcha98.remote.core.bootstrap;


import io.github.hongcha98.remote.common.exception.RemoteException;
import io.github.hongcha98.remote.core.config.RemoteConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteClientBootStrap extends AbstractBootStrap {
    Bootstrap bootstrap;

    EventLoopGroup boos;

    Map<SocketAddress, Channel> socketAddressChannelMap = new ConcurrentHashMap<>();

    public RemoteClientBootStrap(RemoteConfig config) {
        super(config);
    }

    @Override
    public void doStart() {
        bootstrap = new Bootstrap();

        boos = new NioEventLoopGroup(config.getWorkThreadNum());

        bootstrap
                .group(boos)
                .channel(NioSocketChannel.class)
                .handler(channelInitializer());
    }

    @Override
    public void doClose() {
        boos.shutdownGracefully();
    }


    public Channel connect(String host, int port) {
        return connect(InetSocketAddress.createUnresolved(host, port));
    }


    public Channel connect(SocketAddress address) {
        Channel channel = socketAddressChannelMap.get(address);
        if (channel == null || (channel != null && !channel.isActive())) {
            synchronized (this) {
                channel = socketAddressChannelMap.get(address);
                if (channel == null || (channel != null && !channel.isActive())) {
                    channel = doConnect(address);
                    socketAddressChannelMap.put(address, channel);
                }
            }
        }
        return channel;

    }


    private Channel doConnect(SocketAddress remoteAddress) {
        try {
            ChannelFuture connectFuture = bootstrap.connect(remoteAddress).sync();
            if (!connectFuture.isSuccess()) {
                throw new RemoteException(connectFuture.cause());
            }
            return connectFuture.channel();
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }
}
