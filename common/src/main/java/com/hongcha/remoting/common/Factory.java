package com.hongcha.remoting.common;


import java.util.Collection;

public interface Factory<K, V> {

    void register(K key,V value);

    void register(Pair<K, V> pair);


    V getObject(K key);


    Collection<V> getAll();

}
