package com.hongcha.remoting.filter.provider;


import com.hongcha.remoting.common.dto.RequestMessage;

/**
 * 过滤链
 */
public interface ProviderFilterChain {
    /**
     * 继续下一个过滤器处理
     *
     * @return
     */
    RequestMessage process(RequestMessage requestMessage);

}
