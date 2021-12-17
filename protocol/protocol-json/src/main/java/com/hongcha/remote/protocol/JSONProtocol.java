package com.hongcha.remote.protocol;

import com.alibaba.fastjson.JSON;

/**
 * json序列化 对应的协议码 为 0
 */
public class JSONProtocol extends AbstractProtocol {

    @Override
    protected byte[] doEncode(Object o) throws Exception {
        return JSON.toJSONBytes(o);
    }

    @Override
    protected <T> T doDecode(byte[] bytes, Class<T> clazz) throws Exception {
        return JSON.parseObject(bytes, clazz);
    }

    @Override
    public String getProtocolName() {
        return "json";
    }

}
