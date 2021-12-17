package com.hongcha.remote.common;


import java.util.Collection;

public interface Factory<K, V> {

    default void register(K key, V value) {
        register(new Pair<>(key, value));
    }


    void register(Pair<K, V> pair);


    V getValue(K key);

    Collection<K> getKeys();

    Collection<V> getValues();

}
