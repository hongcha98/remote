package com.hongcha.remoting.core;


import com.hongcha.remoting.common.dto.RequestCommon;
import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.core.bootstrap.RemotingClientBootStrap;
import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class RemotingClient extends AbstractRemoting<RemotingClientBootStrap> {

    private RemotingClientBootStrap remotingClientBootStrap;

    public RemotingClient(RemotingConfig config) {
        super(config);
    }

    @Override
    public void init() throws Exception {
        remotingClientBootStrap = new RemotingClientBootStrap(getConfig()) {
            @Override
            public ChannelHandler[] getHandlerArray() {
                return new ChannelHandler[]{
                        new ClientHandler()
                };
            }
        };
        super.init();
    }

    @Override
    public RemotingClientBootStrap getBootStrap() {
        return remotingClientBootStrap;
    }


    public CompletableFuture<RequestCommon> send(String host, int port, RequestMessage requestMessage) throws InterruptedException, ExecutionException {
        Channel channel = remotingClientBootStrap.connect(host, port);
        return asyncSend(channel, requestMessage).getFuture();
    }

    public CompletableFuture<RequestCommon> send(SocketAddress socketAddress, RequestMessage requestMessage) throws InterruptedException, ExecutionException {
        Channel channel = remotingClientBootStrap.connect(socketAddress);
        return asyncSend(channel, requestMessage).getFuture();
    }


    public class ClientHandler extends AbstractHandler {


    }


}
