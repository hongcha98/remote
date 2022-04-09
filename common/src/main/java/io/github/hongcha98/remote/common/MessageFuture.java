package io.github.hongcha98.remote.common;


import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class MessageFuture {
    /**
     * 请求的内容
     */
    private Message message;
    /**
     * 返回的内容 未来式, 也可能不返回
     */
    private CompletableFuture<Message> respFuture;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 超时时间
     */
    private long timeoutTime;


    public MessageFuture(Message message, long timeOut, TimeUnit timeUnit) {
        this.message = message;
        this.respFuture = new CompletableFuture<>();
        this.createTime = System.currentTimeMillis();
        this.timeoutTime = timeUnit.toMillis(timeOut);
    }

    public Message getMessage() {
        return message;
    }

    public CompletableFuture<Message> getRespFuture() {
        return respFuture;
    }

    public boolean isTimeOut() {
        return System.currentTimeMillis() - createTime > timeoutTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageFuture that = (MessageFuture) o;
        return Objects.equals(message, that.message) && Objects.equals(respFuture, that.respFuture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, respFuture);
    }
}
