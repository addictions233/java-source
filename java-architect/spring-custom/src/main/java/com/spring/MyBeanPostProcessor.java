package com.spring;

import java.lang.reflect.Field;

/**
 * @author one
 * @description 针对所有Bean对象创建过程都会调用的处理器, 有多少个Bean对象需要创建,前置处理器就会调用多少次
 * @date 2023-1-3
 */
@Component // 让spring容器进行管理
public class MyBeanPostProcessor implements BeanPostProcessor {
    /**
     * Bean对象在初始化前进行调用
     *
     * @param bean bean对象
     * @param beanName bean对象名称
     * @return Object
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("bean 对象创建前进行了调用");
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    /**
     * Bean对象在初始化后进行调用
     *
     * @param bean bean对象
     * @param beanName bean对象名称
     * @return Object
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // IOC容器中bean对象的名称beanName是唯一的
        if ("userService".equals(beanName)) { // 如果不加这个判断,所有的bean对象在创建时都会调用, 加了之后只有userServiceBean对象在创建时调用
            System.out.println("userService bean对象创建后进行了调用");
        }
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Value.class)) {
                try {
                    field.set(bean, field.getAnnotation(Value.class).value());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
