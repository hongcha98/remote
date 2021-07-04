package com.hongcha.remoting.core;


import com.hongcha.remoting.common.dto.RequestCommon;
import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.core.bootstrap.RemotingClinetBootStrap;
import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;


public class RemotingClient extends AbstractRemoting<RemotingClinetBootStrap> {

    private RemotingClinetBootStrap remotingClinetBootStrap;

    public RemotingClient(RemotingConfig config) {
        super(config);
    }

    @Override
    public void init() throws Exception {
        remotingClinetBootStrap = new RemotingClinetBootStrap(getConfig()) {
            @Override
            public ChannelHandler[] getHandlerArray() {
                return new ChannelHandler[]{
                        new ClinetHandler()
                };
            }
        };
        super.init();
    }

    @Override
    public RemotingClinetBootStrap getBootStrap() {
        return remotingClinetBootStrap;
    }


    public CompletableFuture<RequestCommon> send(String host, int port, RequestMessage requestMessage) throws InterruptedException {
        Channel channel = remotingClinetBootStrap.connect(host, port);
        return send(channel, requestMessage);
    }

    public CompletableFuture<RequestCommon> send(SocketAddress socketAddress, RequestMessage requestMessage) throws InterruptedException {
        Channel channel = remotingClinetBootStrap.connect(socketAddress);
        return send(channel, requestMessage);
    }


    public class ClinetHandler extends AbstractHandler {


    }


}
