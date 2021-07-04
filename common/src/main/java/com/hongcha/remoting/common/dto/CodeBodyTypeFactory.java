package com.hongcha.remoting.common.dto;

import com.hongcha.remoting.common.AbstractSpiLoad;


public class CodeBodyTypeFactory extends AbstractSpiLoad<Integer, Class<?>> {
    private static final String SPI_NAME = "META-INF/remoting-code-body-type";

    @Override
    protected String getSpiRosourcesName() {
        return SPI_NAME;
    }

    @Override
    protected Integer k(String keyStr) {
        return Integer.valueOf(keyStr);
    }

    @Override
    protected Class<?> v(String valueStr) {
        try {
            return Class.forName(valueStr);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
