package com.hongcha.remoting.protocol;


import com.hongcha.remoting.common.util.Assert;

public abstract class AbstractProtocol implements Protocol {

    @Override
    public byte[] encode(Object o) {
        Assert.notNull(o, "serialization object can not be null");
        try {
            return duSerialization(o);
        } catch (Exception e) {
            throw ProtocolException.encode(o.getClass(), this, e);
        }
    }

    protected abstract byte[] duSerialization(Object o) throws Exception;


    @Override
    public <T> T decode(byte[] bytes, Class<T> clazz) {
        Assert.notNull(bytes, "deserialization bytes can not be null");
        Assert.notNull(clazz, "deserialization to class can not be null");
        try {
            return doDeserialization(bytes, clazz);
        } catch (Exception e) {
            throw ProtocolException.decode(clazz, this, e);
        }
    }

    protected abstract <T> T doDeserialization(byte[] bytes, Class<T> clazz) throws Exception;
}
