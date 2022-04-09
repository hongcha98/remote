package io.github.hongcha98.remote.core;

import io.github.hongcha98.remote.core.config.RemoteConfig;
import io.github.hongcha98.remote.core.util.ProtocolUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class RemoteTest {
    RemoteServer remoteServer;
    RemoteClient remoteClient;
    RemoteConfig remoteConfig;

    @Before
    public void before() {
        remoteConfig = new RemoteConfig();
        remoteConfig.setPort(9999);
        remoteServer = new RemoteServer(remoteConfig);
        remoteClient = new RemoteClient(remoteConfig);
    }

    @After
    public void after() {
        remoteClient.close();
        remoteServer.close();
    }

    @Test
    public void echoTest() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        remoteServer.registerProcess(1, (ctx, req) -> {
            User user = ProtocolUtils.decode(req, User.class);
            System.out.println(user);
            countDownLatch.countDown();
        }, Executors.newSingleThreadExecutor());
        remoteServer.start();
        remoteClient.start();
        remoteClient.send(remoteConfig.getHost(), remoteConfig.getPort(), remoteClient.buildRequest(new User("张三", 18), 1));
        countDownLatch.await();
    }

    @Test
    public void sendResponseTest() {
        remoteServer.registerProcess(1, (ctx, req) -> {
            ctx.writeAndFlush(remoteServer.buildResponse(req, "Hello World :" + new String(req.getBody()), 1));
        }, Executors.newSingleThreadExecutor());
        remoteServer.start();
        remoteClient.start();
        String response = remoteClient.send(remoteConfig.getHost(), remoteConfig.getPort(), remoteClient.buildRequest(new User("张三", 18), 1), String.class);
        System.out.println(response);
    }

    @Test
    public void errorTest() {
        remoteServer.registerProcess(1, (ctx, req) -> {
            ctx.writeAndFlush(remoteServer.buildError(req, new NullPointerException("该数据不存在")));
        }, Executors.newSingleThreadExecutor());
        remoteServer.start();
        remoteClient.start();
        try {
            String response = remoteClient.send(remoteConfig.getHost(), remoteConfig.getPort(), remoteClient.buildRequest(new User("张三", 18), 1), String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
