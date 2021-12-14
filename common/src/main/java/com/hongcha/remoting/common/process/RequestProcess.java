package com.hongcha.remoting.common.process;


import com.hongcha.remoting.common.dto.RequestMessage;

/**
 * 请求处理
 */

public interface RequestProcess {

    /**
     * 处理请求并返回数据,如果不为nul将l会把结果写入到通道中
     *
     * @param

     * @return 返回内容
     */
    RequestMessage proess(RequestMessage RequestMessage);

}
