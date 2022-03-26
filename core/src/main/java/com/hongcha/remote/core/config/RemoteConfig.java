package com.hongcha.remote.core.config;


public class RemoteConfig {
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


    private int backlog = 100;


    private int timeOut = 30000;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBossThreadNum() {
        return bossThreadNum;
    }

    public void setBossThreadNum(int bossThreadNum) {
        this.bossThreadNum = bossThreadNum;
    }

    public int getWorkThreadNum() {
        return workThreadNum;
    }

    public void setWorkThreadNum(int workThreadNum) {
        this.workThreadNum = workThreadNum;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
}
