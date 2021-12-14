package com.hongcha.remoting.filter.consumer;


import com.hongcha.remoting.common.MessageFuture;
import com.hongcha.remoting.common.dto.RequestCommon;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;


public class DefaultRequestFilterChin implements RequestFilterChain {

    protected Iterator<RequestFilter> filterIterator;

    protected Supplier<MessageFuture> supplier;


    public DefaultRequestFilterChin(Collection<RequestFilter> providerFilters, Supplier<MessageFuture> supplier) {
        if (providerFilters == null) {
            providerFilters = new LinkedList<>();
        }
        filterIterator = providerFilters.iterator();
        this.supplier = supplier;

    }


    @Override
    public MessageFuture process(RequestCommon requestCommon) throws ExecutionException, InterruptedException {
        if (filterIterator.hasNext()) {
            return filterIterator.next().filter(requestCommon, this);
        }
        return supplier.get();
    }
}
