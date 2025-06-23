package com.one.springboot;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author one
 * @description 自定义的@ConditionalOnClass注解
 * @date 2024-2-12
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(ConditionOnClass.class)
public @interface MyConditionalOnClass {

    String value();
}
