package com.hongcha.remoting.core;

import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RemotingTest {

    RemotingConfig remotingConfig;

    RemotingServer remotingServer;

    RemotingClient remotingClient;

    EventLoopGroup serverEventLoopGroup;


    EventLoopGroup clientEventLoopGroup;

    @Before
    public void init() throws Exception {
        remotingConfig = new RemotingConfig();
        remotingConfig.setPort(9999);
        remotingServer = new RemotingServer(remotingConfig);
        remotingClient = new RemotingClient(remotingConfig);
        remotingServer.init();
        remotingClient.init();
        serverEventLoopGroup = new NioEventLoopGroup(1);
        clientEventLoopGroup = new NioEventLoopGroup(1);

    }

    @After
    public void close() throws Exception {
        remotingClient.close();
        remotingServer.close();
        serverEventLoopGroup.shutdownGracefully();
        clientEventLoopGroup.shutdownGracefully();
    }


    @Test
    public void echo() throws Exception {
        remotingServer.start();
        remotingClient.start();

        RemotingFactory.getCodeBodyTypeFactory().register(1, Long.class);

        remotingServer.registerProcess(1, msg -> {
            String body = (String) msg.getMsg();
            System.out.println("获取请求内容 : " + body);
            return null;
        }, serverEventLoopGroup);
        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setCode(1);
        requestMessage.setProtocol((byte) 2);
        requestMessage.setDirection((byte) 0);
        requestMessage.setHeaders(null);
        requestMessage.setMsg("哈哈哈  真搞笑");

        for (int i = 0; i < 1000; i++) {
            remotingClient.send(remotingConfig.getHost(), remotingConfig.getPort(), requestMessage);
        }

    }


}
