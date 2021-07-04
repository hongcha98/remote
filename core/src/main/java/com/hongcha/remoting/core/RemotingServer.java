package com.hongcha.remoting.core;


import com.hongcha.remoting.core.bootstrap.RemotingServerBootStrap;
import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.channel.ChannelHandler;

public class RemotingServer extends AbstractRemoting<RemotingServerBootStrap> {

    protected RemotingServerBootStrap remotingServerBootStrap;

    public RemotingServer(RemotingConfig config) {
        super(config);
    }

    @Override
    public void init() throws Exception {
        remotingServerBootStrap = new RemotingServerBootStrap(getConfig()) {
            @Override
            public ChannelHandler[] getHandlerArray() {
                return new ChannelHandler[]{
                        new ServerHandler()
                };
            }
        };
        super.init();
    }

    @Override
    protected RemotingServerBootStrap getBootStrap() {
        return remotingServerBootStrap;
    }


    public class ServerHandler extends AbstractHandler {

    }

}
