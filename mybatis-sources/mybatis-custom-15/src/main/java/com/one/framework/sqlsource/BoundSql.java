package com.one.framework.sqlsource;


import java.util.List;

/**
 * @ClassName: BoundSql
 * @Description: 封装解析之后的sql语句和对应的参数信息
 * @Author: one
 * @Date: 2022/03/07
 */
public class BoundSql {

    /**
     * 封装解析之后的sql语句, 去掉了所有的动态标签, #{}, ${}, 变成JDBC可以直接执行的sql语句
     */
    private String sql;

    /**
     * 用list集合封装sql中对应的参数信息, 一个#{}对应一个ParameterMapping
     */
    private List<ParameterMapping> parameterMappings;

    public BoundSql(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void addParameterMapping(ParameterMapping parameterMapping) {
        this.parameterMappings.add(parameterMapping);
    }
}
