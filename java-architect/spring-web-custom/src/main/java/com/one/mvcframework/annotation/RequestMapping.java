package com.one.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @author one
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value();
}
