package com.hongcha.remote.filter.provider;

import com.hongcha.remote.common.RequestCommon;


public interface ResponseFilter {

    void filter(RequestCommon requestCommon, ResponseFilterChain chin);

}
