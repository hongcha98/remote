package com.hongcha.remoting.filter.provider;

import com.hongcha.remoting.common.AbstractSpiLoad;
import com.hongcha.remoting.common.util.ClassUtils;


public class ProviderFilterFactory extends AbstractSpiLoad<String, ProviderFilter> {
    private static final String FILTER_SPI = "META-INF/remoting-filter-provider";

    @Override
    protected String getSpiRosourcesName() {
        return FILTER_SPI;
    }

    @Override
    protected String k(String keyStr) {
        return keyStr;
    }

    @Override
    protected ProviderFilter v(String valueStr) {
        try {
            return ClassUtils.instantiate(valueStr, ProviderFilter.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
