package com.hongcha.example.echo;

import com.hongcha.example.po.User;
import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.core.RemotingClient;
import com.hongcha.remoting.core.config.RemotingConfig;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EchoClient {
    public static void main(String[] args) throws Exception {
        RemotingConfig config = new RemotingConfig();
        config.setPort(9999);

        RemotingClient remotingClient = new RemotingClient(config);

        remotingClient.init();

        remotingClient.start();
        RequestMessage req = new RequestMessage<>();
        req.setCode(1);
        req.setProtocol((byte) 3);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            User.Demo user = User.Demo.newBuilder()
                    .setName(new String(scanner.nextLine().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))
                    .setAge(19).build();

            req.setMsg(user);


            remotingClient.send(config.getHost(), config.getPort(), req);
        }
    }
}
