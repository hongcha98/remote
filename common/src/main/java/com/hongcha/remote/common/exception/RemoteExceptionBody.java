package com.hongcha.remote.common.exception;

import com.hongcha.remote.common.util.ExceptionUtils;

public class RemoteExceptionBody {
    private String message;
    private StackTraceElement[] stackTraceElements;

    public RemoteExceptionBody() {

    }

    public RemoteExceptionBody(Throwable e) {
        this.message = e.getMessage();
        stackTraceElements = ExceptionUtils.getAllStackTraceElements(e);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }

    public void setStackTraceElements(StackTraceElement[] stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }
}
