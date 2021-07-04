package com.hongcha.remoting.filter;

import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.common.process.RequestProcess;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class DefaultFilterChin implements FilterChin {
    protected Iterator<Filter> filterIterator;

    protected RequestProcess requestProcess;

    public DefaultFilterChin(Collection<Filter> filters, RequestProcess requestProcess) {
        if (filters == null) {
            filters = new LinkedList<>();
        }
        filterIterator = filters.iterator();
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
