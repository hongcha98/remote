package com.hongcha.remoting.protocol;


import com.hongcha.remoting.common.AbstractSpiLoad;
import com.hongcha.remoting.common.util.ClassUtils;

public class ProtocolFactory extends AbstractSpiLoad<Byte, Protocol> {
    private static final String PROTOCOL_SPI = "META-INF/remoting-protocol";

    @Override
    protected String getSpiRosourcesName() {
        return PROTOCOL_SPI;
    }

    @Override
    protected Byte k(String keyStr) {
        return Byte.valueOf(keyStr);
    }

    @Override
    protected Protocol v(String valueStr) {
        try {
            return ClassUtils.instantiate(valueStr, Protocol.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
