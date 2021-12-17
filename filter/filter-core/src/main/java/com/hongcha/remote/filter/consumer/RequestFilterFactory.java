package com.hongcha.remote.filter.consumer;

import com.hongcha.remote.common.AbstractSpiLoad;
import com.hongcha.remote.common.util.ClassUtils;


public class RequestFilterFactory extends AbstractSpiLoad<String, RequestFilter> {
    private static final String FILTER_SPI = "META-INF/remote-filter-request";

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
