package com.hongcha.remote.core.bootstrap;


import com.hongcha.remote.core.config.RemoteConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
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
    public void doStart() throws Exception {
        bootstrap = new Bootstrap();

        boos = new NioEventLoopGroup(config.getBossThreadNum());

        bootstrap
                .group(boos)
                .channel(NioSocketChannel.class)
                .handler(channelInitializer());
    }

    @Override
    public void doClose() throws Exception {
        boos.shutdownGracefully();
    }


    public Channel connect(String inetHost, int inetPort) throws InterruptedException {
        return connect(InetSocketAddress.createUnresolved(inetHost, inetPort));
    }


    public Channel connect(InetAddress inetHost, int inetPort) throws InterruptedException {
        return connect(new InetSocketAddress(inetHost, inetPort));
    }


    public Channel connect(SocketAddress remoteAddress) throws InterruptedException {
        Channel channel = socketAddressChannelMap.get(remoteAddress);
        if (channel == null || (channel != null && !channel.isActive())) {
            synchronized (this) {
                channel = socketAddressChannelMap.get(remoteAddress);
                if (channel == null || (channel != null && !channel.isActive())) {
                    channel = doConnect(remoteAddress);
                    socketAddressChannelMap.put(remoteAddress, channel);
                }
            }
        }
        return channel;

    }


    private Channel doConnect(SocketAddress remoteAddress) throws InterruptedException {
        ChannelFuture connectFuture = bootstrap.connect(remoteAddress).sync();
        if (!connectFuture.isSuccess()) {
            throw new RuntimeException("connect error ", connectFuture.cause());
        }
        return connectFuture.channel();
    }


}
