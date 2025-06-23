package com.one.framework.sqlsource;

/**
 * @ClassName: ParameterMapping
 * @Description: 封装#{}中解析出来的参数名称和参数类型
 * @Author: one
 * @Date: 2022/03/07
 */
public class ParameterMapping {

    /**
     * 参数名称, 即#{}中的内容
     */
    private String name;

    /**
     * 参数类型
     */
    private Class<?> parameterType;

    public ParameterMapping(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }
}
