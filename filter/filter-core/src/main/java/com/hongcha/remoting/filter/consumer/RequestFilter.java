package com.hongcha.remoting.filter.consumer;



import com.hongcha.remoting.common.MessageFuture;
import com.hongcha.remoting.common.dto.RequestCommon;

import java.util.concurrent.ExecutionException;

public interface RequestFilter {

    MessageFuture filter(RequestCommon requestCommon, RequestFilterChain chain) throws ExecutionException, InterruptedException;
    
}
