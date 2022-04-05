package com.hongcha.remote.common.spi;

import org.junit.Test;

import java.util.List;

public class SpiTest {
    @Test
    public void multipleSpiTest(){
        List<SpiTestService> spiTestServices = SpiLoader.loadAll(SpiTestService.class);
        System.out.println(spiTestServices);
    }
}
