package com.spring;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Component {
    /**
     * 用于定义bean对象的名称
     *
     * @return String
     */
    String value() default "";
}
