package com.hongcha.remote.filter.provider;


import com.hongcha.remote.common.RequestCommon;

/**
 * 过滤链
 */
public interface ResponseFilterChain {
    /**
     * 继续下一个过滤器处理
     *
     * @return
     */
    void process(RequestCommon requestCommon);

}
