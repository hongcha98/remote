package com.hongcha.remoting.protocol;

import com.sun.istack.internal.NotNull;

/**
 * message的协议
 */
public interface Protocol {

    /**
     * 对象序列化
     * 可能会抛出
     *
     * @param o
     * @return
     */
    byte[] encode(@NotNull Object o);

    /**
     * 字节反序列话为对象
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T decode(@NotNull byte[] bytes, @NotNull Class<T> clazz);

    /**
     * 获取协议名称
     *
     * @return
     */
    String getProtocolName();

}
