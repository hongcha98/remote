package com.hongcha.remoting.common.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RequestMessage<T> {
    private int code;

    private byte protocol;

    private byte direction;

    private Map<String, String> headers;

    private T msg;

}

