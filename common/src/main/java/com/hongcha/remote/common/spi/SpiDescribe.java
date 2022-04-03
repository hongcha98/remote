package com.hongcha.remote.common.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpiDescribe {
    /**
     * spi实例对象的名字
     *
     * @return
     */
    String name();

    /**
     * code 有时候用这个快速定位
     *
     * @return
     */
    int code() default 0;

    /**
     * 优先级  越小越大
     *
     * @return
     */
    int order() default 0;
}
