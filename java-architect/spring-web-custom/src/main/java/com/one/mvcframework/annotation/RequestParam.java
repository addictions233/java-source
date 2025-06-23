package com.one.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @author one
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value();
}
