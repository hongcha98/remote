package io.github.hongcha98.remote.common.exception;

import io.github.hongcha98.remote.common.util.ExceptionUtils;

public class RemoteExceptionBody {
    private String message;
    private String stackTraceElementsMessage;

    public RemoteExceptionBody() {
    }

    public RemoteExceptionBody(Throwable e) {
        this.message = e.getClass().getName() + " : " + e.getMessage();
        this.stackTraceElementsMessage = ExceptionUtils.buildStackTraceElementString(e);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTraceElementsMessage() {
        return stackTraceElementsMessage;
    }

    public void setStackTraceElementsMessage(String stackTraceElementsMessage) {
        this.stackTraceElementsMessage = stackTraceElementsMessage;
    }

    @Override
    public String toString() {
        return "RemoteExceptionBody{" +
                "message='" + message + '\'' +
                ", stackTraceElementsMessage='" + stackTraceElementsMessage + '\'' +
                '}';
    }
}
