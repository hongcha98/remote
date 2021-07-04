package com.hongcha.remoting.core.config;

import lombok.Data;

@Data
public class RemotingConfig {
    /**
     *
     */
    private String host = "localhost";
    /**
     * 监听的端口号
     */
    private int port;

    /**
     * boss group 的线程数
     */
    private int bossThreadNum = 1;

    /**
     * work group的线程数
     */
    private int workThreadNum = 8;


    private int backlog = 1024 * 1024 * 10;


}
