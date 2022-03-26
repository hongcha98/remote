package com.hongcha.remote.core;


import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.core.bootstrap.RemoteClientBootStrap;
import com.hongcha.remote.core.config.RemoteConfig;
import com.hongcha.remote.core.generator.AtomicIntegerIDGenerator;
import com.hongcha.remote.core.generator.IDGenerator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class RemoteClient extends AbstractRemote<RemoteClientBootStrap> {

    private RemoteClientBootStrap RemoteClientBootStrap;

    private IDGenerator idGenerator = new AtomicIntegerIDGenerator();

    private ChannelHandler channelHandler = new ClientHandler();

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
                return new ChannelHandler[]{channelHandler};
            }
        };
        super.start();
    }

    @Override
    protected IDGenerator getIDGenerator() {
        return idGenerator;
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
