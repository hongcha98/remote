package com.hongcha.example.filter;

import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.filter.Filter;
import com.hongcha.remoting.filter.FilterChin;

public class TimeFilter implements Filter {
    @Override
    public RequestMessage filter(RequestMessage reqMessage, FilterChin chin) {
        long l = System.nanoTime();
        try {
            return chin.process(reqMessage);
        } finally {
            System.out.println("耗时 : " + (System.nanoTime() - l));
        }
    }
}
