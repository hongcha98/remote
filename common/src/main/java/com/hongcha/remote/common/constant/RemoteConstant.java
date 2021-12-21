package com.hongcha.remote.common.constant;

import com.hongcha.remote.common.exception.RemoteExceptionBody;

public interface RemoteConstant {
    // 处理错误返回的code码
    int ERROR_CODE = 500;

    Class<?> ERROR_CODE_BODY_TYPE = RemoteExceptionBody.class;

}
