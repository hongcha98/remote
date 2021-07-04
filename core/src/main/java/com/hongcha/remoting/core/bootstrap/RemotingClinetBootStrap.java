package com.hongcha.remoting.core.bootstrap;


import com.hongcha.remoting.core.Decoder;
import com.hongcha.remoting.core.Encoder;
import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class RemotingClinetBootStrap extends AbstractBootStrap {
    Bootstrap bootstrap;

    EventLoopGroup boos;

    Encoder encoder;

    Map<SocketAddress, Channel> socketAddressChannelMap = new HashMap<>();

    public RemotingClinetBootStrap(RemotingConfig config) {
        super(config);
    }

    @Override
    public void init() {
        bootstrap = new Bootstrap();

        boos = new NioEventLoopGroup(config.getBossThreadNum());

        encoder = new Encoder();

        bootstrap
                .group(boos)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, config.getBacklog())
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch
                                .pipeline()
                                .addLast(
                                        new Decoder(),
                                        encoder
                                )
                                .addLast(getHandlerArray());
                    }
                });
    }

    @Override
    public void start() throws Exception {
        /**
         * 暂未具体逻辑
         */
    }

    @Override
    public void close() throws Exception {
        boos.shutdownGracefully();
    }


    public Channel connect(String inetHost, int inetPort) throws InterruptedException {
        return connect(InetSocketAddress.createUnresolved(inetHost, inetPort));
    }


    public Channel connect(InetAddress inetHost, int inetPort) throws InterruptedException {
        return connect(new InetSocketAddress(inetHost, inetPort));
    }


    public Channel connect(SocketAddress remoteAddress) throws InterruptedException {
        synchronized (this) {
            Channel channel = socketAddressChannelMap.get(remoteAddress);
            if (channel == null || (channel != null && !channel.isActive())) {
                channel = doConnect(remoteAddress);
            }
            socketAddressChannelMap.put(remoteAddress, channel);
            return channel;
        }
    }


    private Channel doConnect(SocketAddress remoteAddress) throws InterruptedException {
        ChannelFuture connectFuture = bootstrap.connect(remoteAddress).sync();
        if (!connectFuture.isSuccess()) {
            throw new RuntimeException("connect error ", connectFuture.cause());
        }
        return connectFuture.channel();
    }


}
