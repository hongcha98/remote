package com.hongcha.remote.common.process;


import com.hongcha.remote.common.RequestCommon;
import io.netty.channel.ChannelHandlerContext;

/**
 * 请求处理
 */

public interface RequestProcess {

    void proess(ChannelHandlerContext ctx, RequestCommon RequestMessage);

}