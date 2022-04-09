package io.github.hongcha98.remote.common;


import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public class MessageFuture {
    /**
     * 请求的内容
     */
    private Message message;
    /**
     * 返回的内容 未来式, 也可能不返回
     */
    private CompletableFuture<Message> respFuture;


    public MessageFuture(Message message) {
        this.message = message;
        this.respFuture = new CompletableFuture<>();
    }

    public Message getMessage() {
        return message;
    }

    public CompletableFuture<Message> getRespFuture() {
        return respFuture;
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
