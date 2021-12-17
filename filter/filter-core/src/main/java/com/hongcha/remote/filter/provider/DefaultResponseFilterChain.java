package com.hongcha.remote.filter.provider;

import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.common.process.RequestProcess;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class DefaultResponseFilterChain implements ResponseFilterChain {
    protected final ChannelHandlerContext ctx;
    protected final Iterator<ResponseFilter> filterIterator;
    protected final RequestProcess requestProcess;

    public DefaultResponseFilterChain(
            Collection<ResponseFilter> responseFilters,
            ChannelHandlerContext ctx,
            RequestProcess requestProcess
    ) {
        if (responseFilters == null) {
            responseFilters = new LinkedList<>();
        }
        filterIterator = responseFilters.iterator();
        this.ctx = ctx;
        this.requestProcess = requestProcess;
    }

    @Override
    public void process(RequestCommon requestCommon) {
        if (filterIterator.hasNext()) {
            filterIterator.next().filter(requestCommon, this);
        }
        requestProcess.process(ctx, requestCommon);
    }
}
