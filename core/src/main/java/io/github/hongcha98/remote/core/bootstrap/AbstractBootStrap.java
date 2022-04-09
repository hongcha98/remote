package io.github.hongcha98.remote.core.bootstrap;


import io.github.hongcha98.remote.common.LifeCycle;
import io.github.hongcha98.remote.core.Decoder;
import io.github.hongcha98.remote.core.Encoder;
import io.github.hongcha98.remote.core.config.RemoteConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

import java.util.concurrent.atomic.AtomicBoolean;


public abstract class AbstractBootStrap implements LifeCycle {
    protected AtomicBoolean status = new AtomicBoolean(false);

    protected RemoteConfig config;

    public AbstractBootStrap(RemoteConfig config) {
        this.config = config;
    }

    public AtomicBoolean getStatus() {
        return status;
    }

    public RemoteConfig getConfig() {
        return config;
    }

    protected ChannelHandler[] getHandlerArray() {
        return new ChannelHandler[0];
    }

    protected ChannelInitializer channelInitializer() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch
                        .pipeline()
                        .addLast(
                                new Decoder(),
                                Encoder.INSTANCE
                        ).addLast(
                                getHandlerArray()
                        );
            }
        };
    }

    @Override
    public void start() {
        if (status.compareAndSet(false, true)) {
            try {
                doStart();
            } catch (Exception e) {
                status.set(false);
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("has been activated");
        }
    }

    protected abstract void doStart() throws Exception;

    @Override
    public void close() {
        if (status.compareAndSet(true, false)) {
            try {
                doClose();
            } catch (Exception e) {
                status.set(true);
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("it has been closed");
        }
    }

    protected abstract void doClose() throws Exception;
}
