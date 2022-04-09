package io.github.hongcha98.remote.protocol;

import io.github.hongcha98.remote.common.spi.SpiDescribe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


@SpiDescribe(name = "java",code = 0)
public class JavaProtocol extends AbstractProtocol {

    @Override
    protected byte[] doEncode(Object o) throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o);
            oos.flush();
            return bos.toByteArray();
        }
    }

    @Override
    protected <T> T doDecode(byte[] bytes, Class<T> clazz) throws Exception {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        }
    }


    @Override
    public String getProtocolName() {
        return "java";
    }


}
