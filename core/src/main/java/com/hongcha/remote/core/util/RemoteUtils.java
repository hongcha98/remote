package com.hongcha.remote.core.util;


import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.common.util.Assert;
import com.hongcha.remote.filter.consumer.RequestFilterFactory;
import com.hongcha.remote.filter.provider.ResponseFilterFactory;
import com.hongcha.remote.protocol.Protocol;
import com.hongcha.remote.protocol.ProtocolFactory;


public class RemoteUtils {

    private static final ProtocolFactory PROTOCOL_FACTORY = new ProtocolFactory();

    private static final ResponseFilterFactory FILTER_PROVIDER_FACTORY = new ResponseFilterFactory();

    private static final RequestFilterFactory FILTER_CONSUMER_FACTORY = new RequestFilterFactory();

    public static <T> T getBody(RequestCommon requestCommon, Class<T> clazz) {
        Protocol protocol = PROTOCOL_FACTORY.getValue(requestCommon.getProtocol());
        Assert.notNull(protocol, "protocol code " + requestCommon.getProtocol() + " not found");
        return (T) protocol.decode(requestCommon.getBody(), clazz);
    }

    public static byte[] encode(byte proto, Object obj) {
        Protocol protocol = PROTOCOL_FACTORY.getValue(proto);
        Assert.notNull(protocol, "protocol code " + proto + " not found");
        return protocol.encode(obj);
    }

    public static ProtocolFactory getProtocolFactory() {
        return PROTOCOL_FACTORY;
    }

    public static ResponseFilterFactory getFilterProviderFactory() {
        return FILTER_PROVIDER_FACTORY;
    }

    public static RequestFilterFactory getFilterConsumerFactory() {
        return FILTER_CONSUMER_FACTORY;
    }


}
