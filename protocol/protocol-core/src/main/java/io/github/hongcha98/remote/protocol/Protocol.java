package io.github.hongcha98.remote.protocol;


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
    byte[] encode(Object o);

    /**
     * 字节反序列化为对象
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T decode(byte[] bytes, Class<T> clazz);

    /**
     * 获取协议名称
     *
     * @return
     */
    String getProtocolName();

}
