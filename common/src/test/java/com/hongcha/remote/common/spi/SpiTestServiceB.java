package com.hongcha.remote.common.spi;

@SpiDescribe(name = "spiTestB")
public class SpiTestServiceB implements SpiTestService {
    @Override
    public void echo(Object obj) {
        System.out.println(obj);
    }
}
