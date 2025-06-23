package com.spring;

/**
 * Bean对象在初始中会调用该接口中的方法
 */
public interface InitializingBean {
    /**
     * Bean对象在初始化时会调用该方法
     */
    void afterPropertiesSet();
}
