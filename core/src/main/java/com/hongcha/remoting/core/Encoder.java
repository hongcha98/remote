package com.hongcha.remoting.core;


import com.hongcha.remoting.common.dto.RequestCommon;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class Encoder extends MessageToByteEncoder<RequestCommon> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RequestCommon msg, ByteBuf out) throws Exception {
        byte[] headers = encodeHeaders(msg.getHeaders());
        int length = RequestCommon.FIXED_PLACEHOLDER_LENGTH + headers.length + msg.getBody().length;
        out.writeInt(length);
        out.writeInt(msg.getId());
        out.writeInt(msg.getCode());
        out.writeByte(msg.getProtocol());
        out.writeByte(msg.getDirection());
        out.writeInt(headers.length);
        out.writeBytes(headers);
        out.writeBytes(msg.getBody());
    }

    private byte[] encodeHeaders(Map<String, String> headers) {
        if (headers.isEmpty())
            return new byte[0];
        List<String> headerList = new LinkedList<>();
        headers.forEach((k, v) -> {
            headerList.add(k + RequestCommon.HEADER_KV_DELIMITER + v);
        });
        return String.join(RequestCommon.HEADER_DELIMITER, headerList).getBytes(StandardCharsets.UTF_8);
    }

}
