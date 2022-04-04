package com.hongcha.remote.core.util;

import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.common.spi.SpiLoader;
import com.hongcha.remote.protocol.Protocol;

public class ProtocolUtils {

    public static byte[] encode(RequestCommon requestCommon, Object msg) {
        return SpiLoader.load(Protocol.class, requestCommon.getProtocol()).encode(msg);
    }

    public static <T> T decode(RequestCommon requestCommon, Class<T> clazz) {
        return SpiLoader.load(Protocol.class, requestCommon.getProtocol()).decode(requestCommon.getBody(), clazz);
    }

}
