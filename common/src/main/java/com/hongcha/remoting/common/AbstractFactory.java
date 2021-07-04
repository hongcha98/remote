package com.hongcha.remoting.common;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public abstract class AbstractFactory<K, V> implements Factory<K, V> {
    protected final Map<K, V> factoryMap = new HashMap<>();

    public AbstractFactory() {
        this.init();
    }

    protected abstract void init();

    @Override
    public void register(K key, V value) {
        register(new Pair<>(key, value));
    }

    @Override
    public void register(Pair<K, V> pair) {
        synchronized (this) {
            if (factoryMap.containsKey(pair.getKey())) {
                throw new RuntimeException(" key : " + pair.getKey() + " existed");
            }
            factoryMap.put(pair.getKey(), pair.getValue());
        }
    }

    @Override
    public V getObject(K key) {
        return factoryMap.get(key);
    }


    @Override
    public Collection<V> getAll() {
        return Collections.unmodifiableCollection(factoryMap.values());
    }

}
