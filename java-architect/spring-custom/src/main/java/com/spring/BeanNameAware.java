package com.spring;

public interface BeanNameAware {
    /**
     * 设置bean对象的名称
     *
     * @param name 传入的名称
     */
    void setBeanName(String name);
}
