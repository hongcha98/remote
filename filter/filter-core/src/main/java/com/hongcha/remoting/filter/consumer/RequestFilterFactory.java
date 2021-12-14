package com.hongcha.remoting.filter.consumer;

import com.hongcha.remoting.common.AbstractSpiLoad;
import com.hongcha.remoting.common.util.ClassUtils;


public class RequestFilterFactory extends AbstractSpiLoad<String, RequestFilter> {
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
    protected RequestFilter v(String valueStr) {
        try {
            return ClassUtils.instantiate(valueStr, RequestFilter.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
