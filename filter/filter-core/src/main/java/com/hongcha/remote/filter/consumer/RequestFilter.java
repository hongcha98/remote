package com.hongcha.remote.filter.consumer;


import com.hongcha.remote.common.MessageFuture;
import com.hongcha.remote.common.RequestCommon;

import java.util.concurrent.ExecutionException;

public interface RequestFilter {

    MessageFuture filter(RequestCommon requestCommon, RequestFilterChain chain) throws ExecutionException, InterruptedException;

}
