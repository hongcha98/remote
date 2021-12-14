package com.hongcha.remoting.core.bootstrap;


import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RemotingServerBootStrap extends AbstractBootStrap {

    ServerBootstrap serverBootstrap;

    EventLoopGroup boos;

    EventLoopGroup work;

    public RemotingServerBootStrap(RemotingConfig config) {
        super(config);
    }

    @Override
    public void doInit() throws Exception {
        serverBootstrap = new ServerBootstrap();

        boos = new NioEventLoopGroup(config.getBossThreadNum());

        work = new NioEventLoopGroup(config.getWorkThreadNum());

    }


    @Override
    public void doStart() throws Exception {
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
