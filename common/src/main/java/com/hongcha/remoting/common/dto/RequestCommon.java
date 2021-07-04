package com.hongcha.remoting.common.dto;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
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
     * 协议类型(body的序列化类型)
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

}
