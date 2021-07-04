package com.hongcha.remoting.core;


import com.hongcha.remoting.common.dto.RequestCommon;
import lombok.Data;

import java.util.concurrent.CompletableFuture;


@Data
public class MessageFuture {

    private RequestCommon requestCommon;

    private CompletableFuture<RequestCommon> future;

    public MessageFuture(RequestCommon requestCommon) {
        this.requestCommon = requestCommon;
        this.future = new CompletableFuture<>();
    }

}
