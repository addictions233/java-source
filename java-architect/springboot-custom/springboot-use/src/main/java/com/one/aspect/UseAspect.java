package com.one.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author one
 * @description TODO
 * @date 2024-2-18
 */
@Aspect
@Component
public class UseAspect {

    @Before("execution(* com.one.service.impl.UseServiceImpl.test())")
    public void beforeAspect() {
        System.out.println("aop 前置通知执行了..");
    }
}
