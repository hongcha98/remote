package io.github.hongcha98.remote.common.spi;

@SpiDescribe(name = "spiTestA")
public class SpiTestServiceA implements SpiTestService {
    @Override
    public void echo(Object obj) {
        System.out.println(obj);
    }
}
