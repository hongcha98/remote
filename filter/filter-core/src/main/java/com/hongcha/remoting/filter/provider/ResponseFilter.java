package com.hongcha.remoting.filter.provider;


import com.hongcha.remoting.common.dto.RequestMessage;

public interface ResponseFilter {

    RequestMessage filter(RequestMessage reqMessage, ResponseFilterChain chin);

}
