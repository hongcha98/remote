package com.hongcha.remote.protocol;

import com.alibaba.fastjson.JSON;
import com.hongcha.remote.common.spi.SpiDescribe;


@SpiDescribe(name = "json",code = 1)
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
