package io.github.hongcha98.remote.core;


import io.github.hongcha98.remote.common.Message;
import io.github.hongcha98.remote.common.spi.SpiLoader;
import io.github.hongcha98.remote.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Map;

@ChannelHandler.Sharable
public class Encoder extends MessageToByteEncoder<Message> {
    public static final Encoder INSTANCE = new Encoder();


    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] headers = encodeHeaders(msg.getHeaders());
        int length = Message.FIXED_PLACEHOLDER_LENGTH + headers.length + msg.getBody().length;
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
        return SpiLoader.load(Protocol.class, "json").encode(headers);
    }

}
