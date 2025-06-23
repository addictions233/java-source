package com.one.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @author one
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowried {
    String value() default "";
}
