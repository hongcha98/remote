package io.github.hongcha98.remote.protocol;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.github.hongcha98.remote.common.spi.SpiDescribe;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@SpiDescribe(name = "kryo", code = 2, order = -1)
public class KryoProtocol extends AbstractProtocol {
    public KryoProtocol() {
    }

    @Override
    protected byte[] doEncode(Object o) throws Exception {
        return KryoUtil.writeToByteArray(o);
    }

    @Override
    protected <T> T doDecode(byte[] bytes, Class<T> clazz) throws Exception {
        return KryoUtil.readFromByteArray(bytes);
    }

    @Override
    public String getProtocolName() {
        return "kryo";
    }

    static class KryoUtil {
        private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = new ThreadLocal<Kryo>() {
            @Override
            protected Kryo initialValue() {
                Kryo kryo = new Kryo();
                kryo.setReferences(true);
                kryo.setRegistrationRequired(false);
                ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                        .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
                return kryo;
            }
        };


        public static Kryo getInstance() {
            return KRYO_THREAD_LOCAL.get();
        }


        public static byte[] writeToByteArray(Object obj) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = getInstance();
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return byteArrayOutputStream.toByteArray();
        }

        public static <T> T readFromByteArray(byte[] byteArray) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = getInstance();
            return (T) kryo.readClassAndObject(input);
        }
    }
}
