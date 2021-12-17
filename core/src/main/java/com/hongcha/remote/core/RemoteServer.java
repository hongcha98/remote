package com.hongcha.remote.core;


import com.hongcha.remote.core.config.RemoteConfig;
import com.hongcha.remote.core.bootstrap.RemoteServerBootStrap;
import io.netty.channel.ChannelHandler;

public class RemoteServer extends AbstractRemote<RemoteServerBootStrap> {

    protected RemoteServerBootStrap RemoteServerBootStrap;

    public RemoteServer(RemoteConfig config) {
        super(config);
    }

    @Override
    public void start() throws Exception {
        RemoteServerBootStrap = new RemoteServerBootStrap(getConfig()) {
            @Override
            public ChannelHandler[] getHandlerArray() {
                return new ChannelHandler[]{
                        new ServerHandler()
                };
            }
        };
        super.start();
    }

    @Override
    protected RemoteServerBootStrap getBootStrap() {
        return RemoteServerBootStrap;
    }


    public class ServerHandler extends AbstractHandler {

    }

}
