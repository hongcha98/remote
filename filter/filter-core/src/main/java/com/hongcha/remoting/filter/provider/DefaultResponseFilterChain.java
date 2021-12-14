package com.hongcha.remoting.filter.provider;

import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.common.process.RequestProcess;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class DefaultResponseFilterChain implements ResponseFilterChain {
    protected Iterator<ResponseFilter> filterIterator;

    protected RequestProcess requestProcess;

    public DefaultResponseFilterChain(Collection<ResponseFilter> responseFilters, RequestProcess requestProcess) {
        if (responseFilters == null) {
            responseFilters = new LinkedList<>();
        }
        filterIterator = responseFilters.iterator();
        this.requestProcess = requestProcess;
    }

    @Override
    public RequestMessage process(RequestMessage requestMessage) {

        if (filterIterator.hasNext()) {
            return filterIterator.next().filter(requestMessage, this);
        }

        return requestProcess.proess(requestMessage);
    }
}
