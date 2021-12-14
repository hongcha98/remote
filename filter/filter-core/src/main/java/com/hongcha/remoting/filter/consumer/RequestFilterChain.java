package com.hongcha.remoting.filter.consumer;


import com.hongcha.remoting.common.MessageFuture;
import com.hongcha.remoting.common.dto.RequestCommon;

import java.util.concurrent.ExecutionException;

public interface RequestFilterChain {
    /**
     * 继续下一个过滤器处理
     *
     * @return
     */
    MessageFuture process(RequestCommon requestCommon) throws ExecutionException, InterruptedException;
}
