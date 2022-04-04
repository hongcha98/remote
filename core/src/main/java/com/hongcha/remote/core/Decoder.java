package com.hongcha.remote.core;


import com.hongcha.remote.common.Message;
import com.hongcha.remote.common.spi.SpiLoader;
import com.hongcha.remote.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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
            int count = Message.FIXED_PLACEHOLDER_LENGTH;
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
            Message message = new Message();
            message.setId(id);
            message.setCode(code);
            message.setProtocol(protocol);
            message.setDirection(direction);
            message.setHeaders(headers);
            message.setBody(body);
            buf.release();
            return message;
        }
        return null;
    }

    private Map<String, String> decodeHeaders(byte[] headerByteArray) {
        return SpiLoader.load(Protocol.class, "json").decode(headerByteArray, Map.class);
    }
}
