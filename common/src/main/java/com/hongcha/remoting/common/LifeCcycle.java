package com.hongcha.remoting.common;

public interface LifeCcycle {

    /**
     * 初始化
     *
     * @throws Exception
     */
    void init() throws Exception;

    /**
     * 启动
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 关闭
     *
     * @throws Exception
     */
    void close() throws Exception;


}
