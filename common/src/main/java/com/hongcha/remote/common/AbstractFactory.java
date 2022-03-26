package com.hongcha.remote.common;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class AbstractFactory<K, V> implements Factory<K, V> {
    protected final Map<K, V> factoryMap = new ConcurrentHashMap<>(16);

    public AbstractFactory() {
        this.init();
    }

    protected abstract void init();

    @Override
    public void register(Pair<K, V> pair) {
        if (factoryMap.containsKey(pair.getKey())) {
            throw new RuntimeException(" key : " + pair.getKey() + " existed");
        }
        factoryMap.put(pair.getKey(), pair.getValue());
    }

    @Override
    public V getValue(K key) {
        return factoryMap.get(key);
    }

    @Override
    public Collection<K> getKeys() {
        return factoryMap.keySet();
    }

    @Override
    public Collection<V> getValues() {
        return factoryMap.values();
    }

}
