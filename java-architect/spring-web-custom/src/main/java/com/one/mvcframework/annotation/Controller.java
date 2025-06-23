package com.one.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @InterfaceName: Service
 * @Description: 自定义的controller层创建bean对象的注解
 * @Author: one
 * @Date: 2022/04/23
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    /**
     * 创建bean对象名称, 使用注解时对于没有默认值的属性必须赋值
     */
    String value() default "";
}
