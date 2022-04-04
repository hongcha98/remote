package com.hongcha.remote.core.util;

import com.hongcha.remote.common.Message;
import com.hongcha.remote.common.spi.SpiLoader;
import com.hongcha.remote.protocol.Protocol;

public class ProtocolUtils {

    public static byte[] encode(Message message, Object msg) {
        return SpiLoader.load(Protocol.class, message.getProtocol()).encode(msg);
    }

    public static <T> T decode(Message message, Class<T> clazz) {
        return SpiLoader.load(Protocol.class, message.getProtocol()).decode(message.getBody(), clazz);
    }

}
