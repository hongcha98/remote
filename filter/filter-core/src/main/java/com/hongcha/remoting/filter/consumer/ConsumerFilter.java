package com.hongcha.remoting.filter.consumer;



import com.hongcha.remoting.common.MessageFuture;
import com.hongcha.remoting.common.dto.RequestCommon;

import java.util.concurrent.ExecutionException;

public interface ConsumerFilter {

    MessageFuture filter(RequestCommon requestCommon, ConsumerFilterChain chain) throws ExecutionException, InterruptedException;
    
}
