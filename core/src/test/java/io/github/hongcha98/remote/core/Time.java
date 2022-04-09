package io.github.hongcha98.remote.core;

public class Time {
    public long start;

    public long end;

    public long timeCost() {
        return end - start;
    }

}
