package com.one.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @InterfaceName: Service
 * @Description: 自定义的service层创建bean对象的注解
 * @Author: one
 * @Date: 2022/04/23
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    /**
     * 创建bean对象名称
     */
    String value() default "";
}
