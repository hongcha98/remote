package io.github.hongcha98.remote.common;

import java.util.HashMap;
import java.util.Map;

/**
 * length id code  protocol direction headerLength  headers body
 */

public class Message {
    /**
     * 固定占位符的长度（除headers和 body)
     */
    public static final int FIXED_PLACEHOLDER_LENGTH = 4 + 4 + 1 + 1 + 4;
    /**
     * 信息id
     */
    private int id;

    /**
     * 信息类型
     */
    private int code;

    /**
     * 协议类型(body的序列化协议)
     */
    private byte protocol;

    /**
     * 传输方向  0：请求  1：返回
     */
    private byte direction;

    /**
     * 自定义header
     */
    private Map<String, String> headers;

    /**
     * 内容
     */
    private byte[] body;


    public Map<String, String> getHeaders() {
        return headers != null ? headers : new HashMap<>();
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body != null ? body : new byte[0];
    }

    public void setBody(byte[] body) {
        this.body = body;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public boolean isResponse() {
        return this.direction == (byte) 1;
    }

}
