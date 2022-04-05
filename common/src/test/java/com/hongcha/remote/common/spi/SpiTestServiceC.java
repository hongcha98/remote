package com.hongcha.remote.common.spi;

@SpiDescribe(name = "spiTestC")
public class SpiTestServiceC implements SpiTestService {
    @Override
    public void echo(Object obj) {
        System.out.println(obj);
    }
}
