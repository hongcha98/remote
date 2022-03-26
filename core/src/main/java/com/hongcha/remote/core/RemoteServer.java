package com.hongcha.remote.core;


import com.hongcha.remote.core.bootstrap.RemoteServerBootStrap;
import com.hongcha.remote.core.config.RemoteConfig;
import com.hongcha.remote.core.generator.AtomicIntegerIDGenerator;
import com.hongcha.remote.core.generator.IDGenerator;
import io.netty.channel.ChannelHandler;

public class RemoteServer extends AbstractRemote<RemoteServerBootStrap> {
    protected RemoteServerBootStrap RemoteServerBootStrap;

    private IDGenerator idGenerator = new AtomicIntegerIDGenerator();

    private ChannelHandler channelHandler = new ServerHandler();

    public RemoteServer(RemoteConfig config) {
        super(config);
    }

    @Override
    public void start() throws Exception {
        RemoteServerBootStrap = new RemoteServerBootStrap(getConfig()) {
            @Override
            public ChannelHandler[] getHandlerArray() {
                return new ChannelHandler[]{channelHandler};
            }
        };
        super.start();
    }

    @Override
    protected RemoteServerBootStrap getBootStrap() {
        return RemoteServerBootStrap;
    }

    @ChannelHandler.Sharable
    public class ServerHandler extends AbstractHandler {

    }

    @Override
    protected IDGenerator getIDGenerator() {
        return idGenerator;
    }
}
