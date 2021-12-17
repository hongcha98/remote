package com.hongcha.remote.core.generator;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerIDGenerator implements IDGenerator {
    private final AtomicInteger id = new AtomicInteger(1);

    @Override
    public int nextId() {
        return id.getAndIncrement();
    }
}
