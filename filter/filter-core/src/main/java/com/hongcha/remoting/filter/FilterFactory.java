package com.hongcha.remoting.filter;

import com.hongcha.remoting.common.AbstractSpiLoad;
import com.hongcha.remoting.common.util.ClassUtils;


public class FilterFactory extends AbstractSpiLoad<String, Filter> {
    private static final String FILTER_SPI = "META-INF/remoting-filter";

    @Override
    protected String getSpiRosourcesName() {
        return FILTER_SPI;
    }

    @Override
    protected String k(String keyStr) {
        return keyStr;
    }

    @Override
    protected Filter v(String valueStr) {
        try {
            return ClassUtils.instantiate(valueStr, Filter.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
