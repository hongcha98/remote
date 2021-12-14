package com.hongcha.remoting.common.dto;

import java.util.Map;
import java.util.Objects;


public class RequestMessage<T> {
    private int code;

    private byte protocol;

    private byte direction;

    private Map<String, String> headers;

    private T msg;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte getProtocol() {
        return protocol;
    }

    public void setProtocol(byte protocol) {
        this.protocol = protocol;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }

    public void setDirection(boolean response) {
        this.direction = response ? (byte) 1 : (byte) 0;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestMessage<?> that = (RequestMessage<?>) o;
        return code == that.code && protocol == that.protocol && direction == that.direction && Objects.equals(headers, that.headers) && Objects.equals(msg, that.msg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, protocol, direction, headers, msg);
    }

}

