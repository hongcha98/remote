package com.hongcha.remoting.core;

public class Time {
    public long start;

    public long end;

    public long timeCost() {
        return end - start;
    }

}
