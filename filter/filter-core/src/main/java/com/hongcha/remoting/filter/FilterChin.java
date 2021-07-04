package com.hongcha.remoting.filter;


import com.hongcha.remoting.common.dto.RequestMessage;

/**
 * 过滤链
 */
public interface FilterChin {
    /**
     * 继续下一个过滤器处理
     *
     * @return
     */
    RequestMessage process(RequestMessage requestMessage);

}
