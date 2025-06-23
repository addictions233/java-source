package com.one.springboot.configuaration;

import com.one.springboot.MyConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author one
 * @description 对aop进行自动配置
 * @date 2024-2-18
 */
@Configuration
public class AopAutoConfiguration implements AutoConfiguration{

    @EnableAspectJAutoProxy
    @Configuration(proxyBeanMethods = false)
    @MyConditionalOnClass("org.aspectj.weaver.Advice")
    static class AspectJAutoConfiguration {

    }
}
