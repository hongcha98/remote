package com.hongcha.remoting.filter.consumer;

import com.hongcha.remoting.common.AbstractSpiLoad;
import com.hongcha.remoting.common.util.ClassUtils;


public class ConsumerFilterFactory extends AbstractSpiLoad<String, ConsumerFilter> {
    private static final String FILTER_SPI = "META-INF/remoting-filter-consumer";

    @Override
    protected String getSpiRosourcesName() {
        return FILTER_SPI;
    }

    @Override
    protected String k(String keyStr) {
        return keyStr;
    }

    @Override
    protected ConsumerFilter v(String valueStr) {
        try {
            return ClassUtils.instantiate(valueStr, ConsumerFilter.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
