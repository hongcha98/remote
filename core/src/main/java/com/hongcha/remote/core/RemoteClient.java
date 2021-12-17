package com.hongcha.remote.core;


import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.core.bootstrap.RemoteClientBootStrap;
import com.hongcha.remote.core.config.RemoteConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class RemoteClient extends AbstractRemote<RemoteClientBootStrap> {

    private RemoteClientBootStrap RemoteClientBootStrap;

    public RemoteClient(RemoteConfig config) {
        super(config);
    }


    @Override
    public RemoteClientBootStrap getBootStrap() {
        return RemoteClientBootStrap;
    }

    @Override
    public void start() throws Exception {
        RemoteClientBootStrap = new RemoteClientBootStrap(getConfig()) {
            @Override
            public ChannelHandler[] getHandlerArray() {
                return new ChannelHandler[]{
                        new ClientHandler()
                };
            }
        };
        super.start();
    }

    public CompletableFuture<RequestCommon> send(String host, int port, RequestCommon requestCommon) throws InterruptedException, ExecutionException {
        Channel channel = RemoteClientBootStrap.connect(host, port);
        return asyncSend(channel, requestCommon).getFuture();
    }

    public CompletableFuture<RequestCommon> send(SocketAddress socketAddress, RequestCommon requestCommon) throws InterruptedException, ExecutionException {
        Channel channel = RemoteClientBootStrap.connect(socketAddress);
        return asyncSend(channel, requestCommon).getFuture();
    }


    public class ClientHandler extends AbstractHandler {


    }


}
