package com.spring;

import java.lang.annotation.*;

/**
 * scope属性值为singleton才需要被ioc容器在启动时创建bean对象, 如果是prototype就不需要,是多例的
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Scope {
    String value() default "singleton";
}
