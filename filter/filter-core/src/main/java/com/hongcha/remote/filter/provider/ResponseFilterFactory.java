package com.hongcha.remote.filter.provider;

import com.hongcha.remote.common.AbstractSpiLoad;
import com.hongcha.remote.common.util.ClassUtils;


public class ResponseFilterFactory extends AbstractSpiLoad<String, ResponseFilter> {
    private static final String FILTER_SPI = "META-INF/remote-filter-response";

    @Override
    protected String getSpiResourcesName() {
        return FILTER_SPI;
    }

    @Override
    protected String k(String keyStr) {
        return keyStr;
    }

    @Override
    protected ResponseFilter v(String valueStr) {
        try {
            return ClassUtils.instantiate(valueStr, ResponseFilter.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
