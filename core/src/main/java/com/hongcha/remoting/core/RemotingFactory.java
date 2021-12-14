package com.hongcha.remoting.core;


import com.hongcha.remoting.common.dto.CodeBodyTypeFactory;
import com.hongcha.remoting.common.dto.RequestCommon;
import com.hongcha.remoting.common.util.Assert;
import com.hongcha.remoting.filter.consumer.RequestFilterFactory;
import com.hongcha.remoting.filter.provider.ResponseFilterFactory;
import com.hongcha.remoting.protocol.Protocol;
import com.hongcha.remoting.protocol.ProtocolFactory;


public class RemotingFactory {

    private static final ProtocolFactory PROTOCOL_FACTORY = new ProtocolFactory();

    private static final CodeBodyTypeFactory CODE_BODY_TYPE_FACTORY = new CodeBodyTypeFactory();

    private static final ResponseFilterFactory FILTER_PROVIDER_FACTORY = new ResponseFilterFactory();

    private static final RequestFilterFactory FILTER_CONSUMER_FACTORY = new RequestFilterFactory();


    public static <T> T getBody(RequestCommon requestCommon) {
        Class<?> clazz = CODE_BODY_TYPE_FACTORY.getValue(requestCommon.getCode());
        Assert.notNull(clazz, "body type code :" + requestCommon.getCode() + " not found");
        Protocol protocol = PROTOCOL_FACTORY.getValue(requestCommon.getProtocol());
        Assert.notNull(protocol, "protocol code " + requestCommon.getProtocol() + " not found");
        return (T) protocol.decode(requestCommon.getBody(), clazz);
    }


    public static CodeBodyTypeFactory getCodeBodyTypeFactory() {
        return CODE_BODY_TYPE_FACTORY;
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
