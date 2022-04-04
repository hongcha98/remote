package com.hongcha.remote.core;

import com.hongcha.remote.common.spi.SpiLoader;
import com.hongcha.remote.protocol.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SpiLoaderTest {
    @Test
    public void protocolAllTest() {
        List<Protocol> load = SpiLoader.loadAll(Protocol.class);
        Assert.assertFalse(load.isEmpty());
        List<Protocol> load2 = SpiLoader.loadAll(Protocol.class);
        Assert.assertEquals(load, load2);
    }

    @Test
    public void protocolCodeTest() {
        Protocol protocol0 = SpiLoader.load(Protocol.class, 0);
        Assert.assertEquals(protocol0.getClass(), JavaProtocol.class);
        Protocol protocol1 = SpiLoader.load(Protocol.class, 1);
        Assert.assertEquals(protocol1.getClass(), JSONProtocol.class);
        Protocol protocol2 = SpiLoader.load(Protocol.class, 2);
        Assert.assertEquals(protocol2.getClass(), KryoProtocol.class);
        Protocol protocol3 = SpiLoader.load(Protocol.class, 3);
        Assert.assertEquals(protocol3.getClass(), ProtobufProtocol.class);
    }

    @Test
    public void protocolNameTest() {
        Protocol protocol0 = SpiLoader.load(Protocol.class, "java");
        Assert.assertEquals(protocol0.getClass(), JavaProtocol.class);
        Protocol protocol1 = SpiLoader.load(Protocol.class, "json");
        Assert.assertEquals(protocol1.getClass(), JSONProtocol.class);
        Protocol protocol2 = SpiLoader.load(Protocol.class, "kryo");
        Assert.assertEquals(protocol2.getClass(), KryoProtocol.class);
        Protocol protocol3 = SpiLoader.load(Protocol.class, "protobuf");
        Assert.assertEquals(protocol3.getClass(), ProtobufProtocol.class);
    }


}
