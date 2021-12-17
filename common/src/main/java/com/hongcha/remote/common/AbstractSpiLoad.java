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
    protected abstract String getSpiRosourcesName();

    @Override
    protected void init() {
        try {
            loadSpi();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadSpi() throws IOException {
        List<Properties> rosources = ResourceUtils.getRosources(this.getClass().getClassLoader(), getSpiRosourcesName());
        rosources.forEach(properties -> {
            properties.forEach((key, value) -> {
                spiRegister(key, value);
            });
        });


    }

    protected void spiRegister(Object key, Object value) {
        register(k(key.toString()), v(value.toString()));
    }


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
    protected abstract V v(String valueStr);


}
