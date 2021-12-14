package com.hongcha.remoting.core.bootstrap;


import com.hongcha.remoting.common.LifeCcycle;
import com.hongcha.remoting.core.Decoder;
import com.hongcha.remoting.core.Encoder;
import com.hongcha.remoting.core.config.RemotingConfig;
import com.hongcha.remoting.core.constant.Status;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;


public abstract class AbstractBootStrap implements LifeCcycle {
    protected Status status = Status.CREATE;

    protected RemotingConfig config;

    public AbstractBootStrap(RemotingConfig config) {
        this.config = config;
    }

    public Status getStatus() {
        return status;
    }

    public RemotingConfig getConfig() {
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
    public void init() throws Exception {
        synchronized (this) {
            switch (status) {
                case CREATE:
                    doInit();
                    break;
                case START:

                case STOP:

                default:
                    throw new RuntimeException(this.getClass().getSimpleName() + " STATUS : " + status.name());
            }
        }
    }

    protected abstract void doInit() throws Exception;


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
