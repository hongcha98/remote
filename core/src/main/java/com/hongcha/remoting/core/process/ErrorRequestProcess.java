package com.hongcha.remoting.core.process;

import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.common.process.RequestProcess;

public class ErrorRequestProcess implements RequestProcess {

    @Override
    public RequestMessage proess(RequestMessage RequestMessage) {
        Throwable throwable = (Throwable) RequestMessage.getMsg();
        throwable.printStackTrace();
        return null;
    }

}
