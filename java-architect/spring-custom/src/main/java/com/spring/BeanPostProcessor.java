package com.spring;

import com.sun.istack.internal.Nullable;

/**
 * Bean对象在创建过程中会调用的钩子函数, spring中的@Autowired注解和@Value注解实现的依赖注入都是在BeanPostProcessor中进行实现执行的
 */
public interface BeanPostProcessor {

    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
