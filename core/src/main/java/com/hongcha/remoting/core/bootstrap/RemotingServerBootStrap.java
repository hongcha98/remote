package com.hongcha.remoting.core.bootstrap;


import com.hongcha.remoting.core.Decoder;
import com.hongcha.remoting.core.Encoder;
import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RemotingServerBootStrap extends AbstractBootStrap {

    ServerBootstrap serverBootstrap;

    EventLoopGroup boos;

    EventLoopGroup work;

    Encoder encoder;

    public RemotingServerBootStrap(RemotingConfig config) {
        super(config);
    }

    @Override
    public void init() {
        serverBootstrap = new ServerBootstrap();

        boos = new NioEventLoopGroup(config.getBossThreadNum());

        work = new NioEventLoopGroup(config.getWorkThreadNum());

        encoder = new Encoder();
    }


    @Override
    public void start() throws Exception {
        serverBootstrap
                .group(boos, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, config.getBacklog())
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch
                                .pipeline()
                                .addLast(
                                        new Decoder(),
                                        encoder
                                ).addLast(getHandlerArray());
                    }
                });
        ChannelFuture bindFuture = serverBootstrap.bind(config.getPort()).sync();
        if (!bindFuture.isSuccess()) {
            throw new RuntimeException("start error ", bindFuture.cause());
        }
    }

    @Override
    public void close() throws Exception {
        boos.shutdownGracefully();
        work.shutdownGracefully();
    }

}
