package com.hongcha.remoting.filter.provider;

import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.common.process.RequestProcess;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class DefaultProviderFilterChain implements ProviderFilterChain {
    protected Iterator<ProviderFilter> filterIterator;

    protected RequestProcess requestProcess;

    public DefaultProviderFilterChain(Collection<ProviderFilter> providerFilters, RequestProcess requestProcess) {
        if (providerFilters == null) {
            providerFilters = new LinkedList<>();
        }
        filterIterator = providerFilters.iterator();
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
