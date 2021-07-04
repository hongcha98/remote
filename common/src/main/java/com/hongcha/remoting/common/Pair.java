package com.hongcha.remoting.common;

import com.hongcha.remoting.common.util.Assert;
import lombok.Data;

/**
 * 描述一对
 *
 * @param <K>
 * @param <V>
 */
@Data

public class Pair<K, V> {

    private K key;

    private V value;

    public Pair(K key, V value) {
        Assert.notNull(key, "key can not be null");
        Assert.notNull(value, "value can not be null");
        this.key = key;
        this.value = value;
    }
}
