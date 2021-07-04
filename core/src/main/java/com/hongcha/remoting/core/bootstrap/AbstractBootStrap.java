package com.hongcha.remoting.core.bootstrap;


import com.hongcha.remoting.common.LifeCcycle;
import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.channel.ChannelHandler;
import lombok.Data;

@Data
public abstract class AbstractBootStrap implements LifeCcycle {

    protected RemotingConfig config;

    public AbstractBootStrap(RemotingConfig config) {
        this.config = config;
    }


    public RemotingConfig getConfig() {
        return config;
    }


    public ChannelHandler[] getHandlerArray() {
        return new ChannelHandler[0];
    }

}
