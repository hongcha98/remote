package io.github.hongcha98.remote.core;


import io.github.hongcha98.remote.core.bootstrap.RemoteServerBootStrap;
import io.github.hongcha98.remote.core.config.RemoteConfig;
import io.github.hongcha98.remote.core.generator.AtomicIntegerIDGenerator;
import io.github.hongcha98.remote.core.generator.IDGenerator;
import io.netty.channel.ChannelHandler;

public class RemoteServer extends AbstractRemote<RemoteServerBootStrap> {
    protected RemoteServerBootStrap RemoteServerBootStrap;

    private IDGenerator idGenerator = new AtomicIntegerIDGenerator();

    private ChannelHandler channelHandler = new ServerHandler();

    public RemoteServer(RemoteConfig config) {
        super(config);
    }

    @Override
    public void start() {
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
