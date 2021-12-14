package com.hongcha.remoting.common.dto;

import java.util.HashMap;
import java.util.Map;

public class RequestCommon {

    /**
     * header k v 分隔符
     */
    public static final String HEADER_KV_DELIMITER = ":KV_\r:";

    /**
     * header与header之间的分隔符
     */
    public static final String HEADER_DELIMITER = ":HEADER_\r:";

    /**
     * 固定占位符的长度（除heraders和body)
     */

    public static final int FIXED_PLACEHOLDER_LENGTH = 4 + 4 + 1 + 1 + 4;

    /**
     * 全部长度
     */
    private int length;

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
     * header长度
     */
    private int headerLength;


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


    public byte[] getBody() {
        return body != null ? body : new byte[0];
    }


    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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

    public int getHeaderLength() {
        return headerLength;
    }

    public void setHeaderLength(int headerLength) {
        this.headerLength = headerLength;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
