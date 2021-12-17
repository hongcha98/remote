package com.hongcha.remote.common;

import com.hongcha.remote.common.util.Assert;

import java.util.Objects;

/**
 * 描述一对
 *
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {

    private K key;

    private V value;

    public Pair(K key, V value) {
        Assert.notNull(key, "key can not be null");
        Assert.notNull(value, "value can not be null");
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
