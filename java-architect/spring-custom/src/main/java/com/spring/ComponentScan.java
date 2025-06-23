package com.spring;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScan {
    /**
     * 用于定义扫描的包路径
     *
     * @return
     */
    String value();
}
