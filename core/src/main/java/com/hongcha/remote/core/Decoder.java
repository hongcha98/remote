package com.hongcha.remote.core;


import com.hongcha.remote.common.RequestCommon;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class Decoder extends LengthFieldBasedFrameDecoder {

    public Decoder() {
        super(1024 * 1024 * 10, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) decode;
            int count = RequestCommon.FIXED_PLACEHOLDER_LENGTH;
            int length = buf.readInt();
            int id = buf.readInt();
            int code = buf.readInt();
            byte protocol = buf.readByte();
            byte direction = buf.readByte();
            int headerLength = buf.readInt();
            Map<String, String> headers = new HashMap<>();
            if (headerLength != 0) {
                count += headerLength;
                byte[] headerByteArray = new byte[headerLength];
                buf.readBytes(headerByteArray);
                headers = decodeHeaders(headerByteArray);
            }
            int bodyLength = length - count;
            byte[] body = new byte[bodyLength];
            buf.readBytes(body);
            RequestCommon requestCommon = new RequestCommon();
            requestCommon.setLength(length);
            requestCommon.setId(id);
            requestCommon.setCode(code);
            requestCommon.setProtocol(protocol);
            requestCommon.setDirection(direction);
            requestCommon.setHeaderLength(headerLength);
            requestCommon.setHeaders(headers);
            requestCommon.setBody(body);
            buf.release();
            return requestCommon;
        }
        return null;
    }

    private Map<String, String> decodeHeaders(byte[] headerByteArray) {
        Map<String, String> map = new HashMap<>();
        String headersStr = new String(headerByteArray, StandardCharsets.UTF_8);
        String[] allHeader = headersStr.split(RequestCommon.HEADER_DELIMITER);
        for (String headerStr : allHeader) {
            String[] headerKV = headerStr.split(RequestCommon.HEADER_KV_DELIMITER);
            map.put(headerKV[0], headerKV[1]);
        }
        return map;
    }
}
