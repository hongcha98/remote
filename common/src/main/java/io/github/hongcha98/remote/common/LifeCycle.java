package io.github.hongcha98.remote.common;

/**
 * 生命周期
 */
public interface LifeCycle {
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
