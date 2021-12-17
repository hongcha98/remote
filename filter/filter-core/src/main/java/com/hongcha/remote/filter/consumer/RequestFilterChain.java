package com.hongcha.remote.filter.consumer;


import com.hongcha.remote.common.MessageFuture;
import com.hongcha.remote.common.RequestCommon;

import java.util.concurrent.ExecutionException;

public interface RequestFilterChain {
    /**
     * 继续下一个过滤器处理
     *
     * @return
     */
    MessageFuture process(RequestCommon requestCommon) throws ExecutionException, InterruptedException;
}
