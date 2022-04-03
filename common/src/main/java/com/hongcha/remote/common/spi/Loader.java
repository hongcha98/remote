package com.hongcha.remote.common.spi;

import java.util.Objects;

public class Loader<T> implements Comparable<Loader<T>> {
    private SpiDescribe spiDescribe;
    private T loader;

    public Loader(SpiDescribe spiDescribe, T loader) {
        this.spiDescribe = spiDescribe;
        this.loader = loader;
    }

    public SpiDescribe getSpiDescribe() {
        return spiDescribe;
    }

    public T getLoader() {
        return loader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loader<?> loader = (Loader<?>) o;
        return Objects.equals(spiDescribe.name(), loader.spiDescribe.name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(spiDescribe.name());
    }

    @Override
    public int compareTo(Loader<T> o) {
        return Integer.compare(spiDescribe.order(), o.getSpiDescribe().order());
    }
}