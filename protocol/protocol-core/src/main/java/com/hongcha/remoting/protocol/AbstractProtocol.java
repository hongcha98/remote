package com.hongcha.remoting.protocol;


import com.hongcha.remoting.common.util.Assert;

public abstract class AbstractProtocol implements Protocol {

    @Override
    public byte[] encode(Object o) {
        Assert.notNull(o, "encode object can not be null");
        try {
            return doEncode(o);
        } catch (Exception e) {
            throw ProtocolException.encode(o.getClass(), this, e);
        }
    }

    protected abstract byte[] doEncode(Object o) throws Exception;


    @Override
    public <T> T decode(byte[] bytes, Class<T> clazz) {
        Assert.notNull(bytes, "decode bytes can not be null");
        Assert.notNull(clazz, "decode to class can not be null");
        try {
            return doDecode(bytes, clazz);
        } catch (Exception e) {
            throw ProtocolException.decode(clazz, this, e);
        }
    }

    protected abstract <T> T doDecode(byte[] bytes, Class<T> clazz) throws Exception;
}
