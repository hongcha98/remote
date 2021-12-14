package com.hongcha.remoting.core;

import com.hongcha.remoting.common.dto.RequestCommon;
import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RemotingTest {

    RemotingConfig remotingConfig;

    RemotingServer remotingServer;

    RemotingClient remotingClient;

    EventLoopGroup serverEventLoopGroup;

    EventLoopGroup clientEventLoopGroup;

    Time time;

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
        remotingServer.start();
        remotingClient.start();
        time = new Time();
        time.start = System.currentTimeMillis();
    }

    @After
    public void close() throws Exception {
        remotingClient.close();
        remotingServer.close();
        time.end = System.currentTimeMillis();
        System.out.println("time.timeCost() = " + time.timeCost());
        serverEventLoopGroup.shutdownGracefully();
        clientEventLoopGroup.shutdownGracefully();
    }


    @Test
    public void echo() throws Exception {


        RemotingFactory.getCodeBodyTypeFactory().register(1, String.class);

        remotingServer.registerProcess(1, msg -> {
            return msg;
//            return null;
        }, serverEventLoopGroup);
        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setCode(1);
        requestMessage.setProtocol((byte) 2);
        requestMessage.setDirection((byte) 0);
        requestMessage.setHeaders(null);
        StringBuilder body = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            body.append("哈");
        }
        requestMessage.setMsg(body.toString());

        List<CompletableFuture<RequestCommon>> list = new LinkedList<>();
        for (int i = 0; i < 100000; i++) {
            list.add(remotingClient.send(remotingConfig.getHost(), remotingConfig.getPort(), requestMessage));
        }

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));

        voidCompletableFuture.get();

        System.out.println(1);

    }


}
