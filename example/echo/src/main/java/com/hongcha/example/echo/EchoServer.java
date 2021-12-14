package com.hongcha.example.echo;

import com.hongcha.example.po.User;
import com.hongcha.remoting.core.RemotingServer;
import com.hongcha.remoting.core.config.RemotingConfig;
import io.netty.channel.nio.NioEventLoopGroup;

public class EchoServer {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup work = new NioEventLoopGroup(8);
        RemotingConfig config = new RemotingConfig();
        config.setPort(9999);
        RemotingServer remotingServer = new RemotingServer(config);

        remotingServer.registerProcess(0, msg -> {
            String m = (String) msg.getMsg();
            System.out.println(m);
            msg.setMsg("clent hello world");
            return null;
        }, work);

        remotingServer.registerProcess(1, msg -> {
            User.Demo msg1 = (User.Demo) msg.getMsg();
            System.out.println("msg1.getName() = " + msg1.getName());
            System.out.println("msg1.getAge() = " + msg1.getAge());
//            msg.setMsg(User.Demo.newBuilder().setName("1").setAge(1).build());
            return null;
        }, work);

        remotingServer.init();

        remotingServer.start();


    }
}
