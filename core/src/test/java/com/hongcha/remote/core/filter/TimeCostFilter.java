package com.hongcha.remote.core.filter;

import com.hongcha.remote.common.MessageFuture;
import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.common.spi.SpiDescribe;
import com.hongcha.remote.filter.consumer.RequestFilter;
import com.hongcha.remote.filter.consumer.RequestFilterChain;

import java.util.concurrent.ExecutionException;

@SpiDescribe(name = "timeCost", order = Integer.MIN_VALUE)
public class TimeCostFilter implements RequestFilter {
    @Override
    public MessageFuture filter(RequestCommon requestCommon, RequestFilterChain chain) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        MessageFuture messageFuture = chain.process(requestCommon);
        messageFuture.getFuture().get();
        System.out.println("耗时 : " + (System.currentTimeMillis() - start));
        return messageFuture;
    }
}
