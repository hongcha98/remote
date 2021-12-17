package com.hongcha.remote.core.bootstrap;


import com.hongcha.remote.common.LifeCycle;
import com.hongcha.remote.core.Decoder;
import com.hongcha.remote.core.config.RemoteConfig;
import com.hongcha.remote.core.Encoder;
import com.hongcha.remote.common.constant.Status;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;


public abstract class AbstractBootStrap implements LifeCycle {
    protected Status status = Status.CREATE;

    protected RemoteConfig config;

    public AbstractBootStrap(RemoteConfig config) {
        this.config = config;
    }

    public Status getStatus() {
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
    public void start() throws Exception {
        synchronized (this) {
            switch (status) {
                case CREATE:
                    doStart();
                    this.status = Status.START;
                    break;
                case START:

                case STOP:

                default:
                    throw new RuntimeException(this.getClass().getSimpleName() + " STATUS : " + status.name());
            }
        }
    }

    protected abstract void doStart() throws Exception;

    @Override
    public void close() throws Exception {
        synchronized (this) {
            switch (status) {
                case CREATE:

                case START:
                    doClose();
                    this.status = Status.STOP;
                    break;
                case STOP:

                default:
                    throw new RuntimeException(this.getClass().getSimpleName() + " STATUS : " + status.name());
            }
        }
    }

    protected abstract void doClose() throws Exception;
}
