package io.github.hongcha98.remote.protocol;

import com.google.protobuf.MessageLite;
import io.github.hongcha98.remote.common.spi.SpiDescribe;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpiDescribe(name = "protobuf", code = 3)
public class ProtobufProtocol extends AbstractProtocol {
    private static Map<Class<?>, Method> PARSE_FROM_MAP = new ConcurrentHashMap<>();


    @Override
    protected byte[] doEncode(Object o) throws Exception {
        if (o instanceof MessageLite) {
            return ((MessageLite) o).toByteArray();
        }
        throw new RuntimeException(o.getClass() + " no protobuf class");
    }

    @Override
    protected <T> T doDecode(byte[] bytes, Class<T> clazz) throws Exception {
        Method parse = PARSE_FROM_MAP.computeIfAbsent(clazz, c -> {
            Method parseFrom = null;
            try {
                parseFrom = c.getDeclaredMethod("parseFrom", byte[].class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            parseFrom.setAccessible(true);
            return parseFrom;
        });
        return (T) parse.invoke(null, bytes);

    }

    @Override
    public String getProtocolName() {
        return "protobuf";
    }
}
