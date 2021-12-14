package com.hongcha.remoting.common;


import com.hongcha.remoting.common.dto.RequestCommon;
import lombok.Data;

import java.util.concurrent.CompletableFuture;


@Data
public class MessageFuture {
    /**
     * 请求的内容
     */
    private RequestCommon requestCommon;
    /**
     * 返回的内容 未来式
     */
    private CompletableFuture<RequestCommon> future;

    public MessageFuture(RequestCommon requestCommon) {
        this.requestCommon = requestCommon;
        this.future = new CompletableFuture<>();
    }

}
