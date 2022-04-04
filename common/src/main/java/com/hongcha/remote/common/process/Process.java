package com.hongcha.remote.common.process;


import com.hongcha.remote.common.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * 请求处理
 */
@FunctionalInterface
public interface Process {
    /**
     * 处理消息
     *
     * @param ctx     上下文
     * @param message 信息,body自己decode到自己所需要的类型
     */
    void process(ChannelHandlerContext ctx, Message message);

}
