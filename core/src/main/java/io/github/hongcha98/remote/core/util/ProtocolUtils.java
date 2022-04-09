package io.github.hongcha98.remote.core.util;

import io.github.hongcha98.remote.common.Message;
import io.github.hongcha98.remote.common.spi.SpiLoader;
import io.github.hongcha98.remote.protocol.Protocol;

public class ProtocolUtils {

    public static byte[] encode(Message message, Object msg) {
        return SpiLoader.load(Protocol.class, message.getProtocol()).encode(msg);
    }

    public static <T> T decode(Message message, Class<T> clazz) {
        return SpiLoader.load(Protocol.class, message.getProtocol()).decode(message.getBody(), clazz);
    }

}
