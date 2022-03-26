package com.hongcha.remote.core.bootstrap;


import com.hongcha.remote.core.config.RemoteConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RemoteServerBootStrap extends AbstractBootStrap {
    ServerBootstrap serverBootstrap;

    EventLoopGroup boos;

    EventLoopGroup work;

    public RemoteServerBootStrap(RemoteConfig config) {
        super(config);
    }

    @Override
    public void doStart() throws Exception {
        serverBootstrap = new ServerBootstrap();
        boos = new NioEventLoopGroup(config.getBossThreadNum());
        work = new NioEventLoopGroup(config.getWorkThreadNum());
        serverBootstrap
                .group(boos, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, config.getBacklog())
                .childHandler(channelInitializer());
        ChannelFuture bindFuture = serverBootstrap.bind(config.getPort()).sync();
        if (!bindFuture.isSuccess()) {
            throw new RuntimeException("start error ", bindFuture.cause());
        }
    }

    @Override
    public void doClose() throws Exception {
        boos.shutdownGracefully();
        work.shutdownGracefully();
    }

}
