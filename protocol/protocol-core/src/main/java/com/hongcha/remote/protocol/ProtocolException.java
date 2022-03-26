package com.hongcha.remote.protocol;

public class ProtocolException extends RuntimeException {

    private ProtocolException(String msg, Protocol protocol) {
        super(
                String.format(
                        "protocol : %s , %s",
                        protocol.getProtocolName(),
                        msg
                )
        );
    }

    private ProtocolException(String msg, Protocol protocol, Throwable throwable) {
        super(
                String.format(
                        "protocol : %s , %s",
                        protocol.getProtocolName(),
                        msg
                ),
                throwable
        );
    }

    public static ProtocolException encode(Class<?> clazz, Protocol protocol) {
        return new ProtocolException(clazz.getName() + " encode failure", protocol);

    }

    public static ProtocolException encode(Class<?> clazz, Protocol protocol, Throwable throwable) {
        return new ProtocolException(clazz.getName() + " encode failure", protocol, throwable);
    }


    public static ProtocolException decode(Class<?> clazz, Protocol protocol) {
        return new ProtocolException(clazz.getName() + " decode failure", protocol);
    }

    public static ProtocolException decode(Class<?> clazz, Protocol protocol, Throwable throwable) {
        return new ProtocolException(clazz.getName() + " decode failure", protocol, throwable);
    }


}
