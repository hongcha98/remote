package com.hongcha.remote.common;


import java.util.Objects;
import java.util.concurrent.CompletableFuture;


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

    public RequestCommon getRequestCommon() {
        return requestCommon;
    }

    public CompletableFuture<RequestCommon> getFuture() {
        return future;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageFuture that = (MessageFuture) o;
        return Objects.equals(requestCommon, that.requestCommon) && Objects.equals(future, that.future);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestCommon, future);
    }
}
