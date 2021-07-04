package com.hongcha.remoting.protocol;

import com.esotericsoftware.kryo.Kryo;

public class KryoProtocol extends AbstractProtocol {
    Kryo kryo;

    public KryoProtocol() {
        kryo = new Kryo();
        kryo.setReferences(false);

    }

    @Override
    protected byte[] duSerialization(Object o) throws Exception {
        return KryoUtil.writeToByteArray(o);
    }

    @Override
    protected <T> T doDeserialization(byte[] bytes, Class<T> clazz) throws Exception {
        return KryoUtil.readFromByteArray(bytes);
    }

    @Override
    public String getProtocolName() {
        return "kryo";
    }


}
