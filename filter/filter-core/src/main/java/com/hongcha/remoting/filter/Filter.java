package com.hongcha.remoting.filter;


import com.hongcha.remoting.common.dto.RequestMessage;

public interface Filter {

    RequestMessage filter(RequestMessage reqMessage, FilterChin chin);

}
