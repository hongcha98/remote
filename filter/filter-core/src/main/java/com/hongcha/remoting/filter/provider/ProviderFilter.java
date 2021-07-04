package com.hongcha.remoting.filter.provider;


import com.hongcha.remoting.common.dto.RequestMessage;

public interface ProviderFilter {

    RequestMessage filter(RequestMessage reqMessage, ProviderFilterChain chin);

}
