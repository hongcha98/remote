package com.hongcha.remote.common;

import com.hongcha.remote.common.util.ResourceUtils;

import java.io.IOException;
import java.util.List;
import java.util.Properties;


/**
 * 抽象的spi加载
 *
 * @param <K>
 * @param <V>
 */
public abstract class AbstractSpiLoad<K, V> extends AbstractFactory<K, V> {


    @Override
    protected void init() {
        try {
            loadSpi();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadSpi() throws IOException {
        List<Properties> resources = ResourceUtils.getResources(this.getClass().getClassLoader(), getSpiResourcesName());
        resources.forEach(properties -> {
            properties.forEach((key, value) -> {
                spiRegister(key, value);
            });
        });
    }


    protected void spiRegister(Object key, Object value) {
        register(k(key.toString()), v(value.toString(), getArgs()));
    }


    protected Object[] getArgs() {
        return new Object[0];
    }


    protected abstract String getSpiResourcesName();

    /**
     * spi读取的key string 转换成具体的类型
     *
     * @param keyStr
     * @return
     */
    protected abstract K k(String keyStr);


    /**
     * spi读取的value string 转换成具体的类型
     *
     * @param valueStr
     * @return
     */
    protected abstract V v(String valueStr, Object... args);

}
