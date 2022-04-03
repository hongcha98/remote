package com.hongcha.remote.common.spi;

public class SpiException extends RuntimeException {
    public SpiException(String message) {
        super(message);
    }

    public SpiException(Throwable cause) {
        super(cause);
    }
}
