package com.spring;

/**
 * @author one
 * @description 封装bean对象的一些基础信息
 * @date 2023-1-2
 */
public class BeanDefinition {

    /**
     * bean对象的类型
     */
    private Class type;

    /**
     * 是单例还是多例
     */
    private String scope;

    /**
     * 是否是懒加载
     */
    private boolean isLazy;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }
}
