package com.hongcha.example.filter;

import com.hongcha.remoting.common.MessageFuture;
import com.hongcha.remoting.common.dto.RequestCommon;
import com.hongcha.remoting.filter.consumer.ConsumerFilter;
import com.hongcha.remoting.filter.consumer.ConsumerFilterChain;

import java.util.concurrent.ExecutionException;

public class TimeConsumerFilter implements ConsumerFilter {
    @Override
    public MessageFuture filter(RequestCommon requestCommon, ConsumerFilterChain chain) throws ExecutionException, InterruptedException {
        long l = System.nanoTime();
        try {
            MessageFuture process = chain.process(requestCommon);
            process.getFuture().get();
            return process;
        } finally {
            System.out.println("耗时 : " + (System.nanoTime() - l));
        }
    }
}
