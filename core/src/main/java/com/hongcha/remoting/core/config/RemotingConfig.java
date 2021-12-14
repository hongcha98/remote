package com.hongcha.remoting.core.config;


import java.util.Objects;

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


    private int backlog = 100;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemotingConfig that = (RemotingConfig) o;
        return port == that.port && bossThreadNum == that.bossThreadNum && workThreadNum == that.workThreadNum && backlog == that.backlog && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, bossThreadNum, workThreadNum, backlog);
    }
}
