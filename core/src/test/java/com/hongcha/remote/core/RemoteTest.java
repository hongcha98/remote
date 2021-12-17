package com.hongcha.remote.core;

import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.core.config.RemoteConfig;
import com.hongcha.remote.core.util.RemoteUtils;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class RemoteTest {

    RemoteConfig remoteConfig;

    RemoteServer remoteServer;

    RemoteClient remoteClient;

    EventLoopGroup serverEventLoopGroup;

    EventLoopGroup clientEventLoopGroup;

    Time time;

    @Before
    public void init() throws Exception {
        remoteConfig = new RemoteConfig();
        remoteConfig.setPort(9999);
        remoteServer = new RemoteServer(remoteConfig);
        remoteClient = new RemoteClient(remoteConfig);
        serverEventLoopGroup = new NioEventLoopGroup(1);
        clientEventLoopGroup = new NioEventLoopGroup(1);
        remoteServer.start();
        remoteClient.start();
        time = new Time();
        time.start = System.currentTimeMillis();
    }

    @After
    public void close() throws Exception {
        remoteClient.close();
        remoteServer.close();
        time.end = System.currentTimeMillis();
        System.out.println("time.timeCost() = " + time.timeCost());
        serverEventLoopGroup.shutdownGracefully();
        clientEventLoopGroup.shutdownGracefully();
    }


    @Test
    public void echo() throws Exception {
        int n = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(n);
        remoteServer.registerProcess(1, (ctx, req) -> {
            String body = RemoteUtils.getBody(req, String.class);
            System.out.println(body);
            countDownLatch.countDown();
        }, new NioEventLoopGroup(1));
        RequestCommon requestCommon = new RequestCommon();
        requestCommon.setCode(1);
        requestCommon.setProtocol((byte) 2);
        requestCommon.setBody(RemoteUtils.encode((byte) 2, "哈哈哈哈"));

        for (int i = 0; i < n; i++) {
            remoteClient.send(remoteConfig.getHost(), remoteConfig.getPort(), requestCommon);
        }

        countDownLatch.await();
    }


}
