package com.hongcha.remote.common.exception;

public class RemoteException extends RuntimeException {

    public RemoteException(Throwable e) {
        super(e);
    }

    public RemoteException(RemoteExceptionBody remoteExceptionBody) {
        super(remoteExceptionBody.getMessage());
        setStackTrace(remoteExceptionBody.getStackTraceElements());
    }

}
