package com.hongcha.remote.core;

import com.hongcha.remote.common.constant.RemoteConstant;
import com.hongcha.remote.common.exception.RemoteExceptionBody;
import com.hongcha.remote.core.config.RemoteConfig;
import com.hongcha.remote.core.util.ProtocolUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;

public class RemoteTest {
    RemoteServer remoteServer;
    RemoteClient remoteClient;
    RemoteConfig remoteConfig;

    @Before
    public void before() throws Exception {
        remoteConfig = new RemoteConfig();
        remoteConfig.setPort(9999);
        remoteServer = new RemoteServer(remoteConfig);
        remoteClient = new RemoteClient(remoteConfig);
    }

    @After
    public void after() throws Exception {
        remoteClient.close();
        remoteServer.close();
    }

    @Test
    public void echoTest() throws Exception {
        remoteServer.registerProcess(1, (ctx, req) -> {
            User user = ProtocolUtils.decode(req, User.class);
            System.out.println(user);
        }, Executors.newSingleThreadExecutor());
        remoteServer.start();
        remoteClient.start();
        remoteClient.send(remoteConfig.getHost(), remoteConfig.getPort(), remoteClient.buildRequest(new User("张三", 18), 1));
        Thread.sleep(1000);
    }

    @Test
    public void sendResponseTest() throws Exception {
        remoteServer.registerProcess(1, (ctx, req) -> {
            ctx.writeAndFlush(remoteServer.buildResponse(req, "Hello World :" + new String(req.getBody()), 1));
        }, Executors.newSingleThreadExecutor());
        remoteServer.start();
        remoteClient.start();
        String response = remoteClient.send(remoteConfig.getHost(), remoteConfig.getPort(), remoteClient.buildRequest(new User("张三", 18), 1), String.class);
        System.out.println(response);
        Thread.sleep(1000);
    }

    @Test
    public void errorTest() throws Exception {
        remoteServer.registerProcess(1, (ctx, req) -> {
            ctx.writeAndFlush(remoteServer.buildResponse(req, new RemoteExceptionBody(new RemoteException("我出错了")), RemoteConstant.ERROR_CODE));
        }, Executors.newSingleThreadExecutor());
        remoteServer.start();
        remoteClient.start();
        String response = remoteClient.send(remoteConfig.getHost(), remoteConfig.getPort(), remoteClient.buildRequest(new User("张三", 18), 1), String.class);
        System.out.println(response);
        Thread.sleep(1000);
    }


}
