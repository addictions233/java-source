package com.one.handler;

import com.one.framework.sqlsource.BoundSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @ClassName: ParameterHandler
 * @Description: 参数处理的处理器
 * @Author: one
 * @Date: 2022/03/19
 */
public interface ParameterHandler {
    /**
     * 参数处理
     * @param parameterTypeClass 参数类型
     * @param statement 预编译执行者对象
     * @param param 查询参数
     * @param boundSql 封装了sql
     * @throws SQLException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    void setParameter(Class<?> parameterTypeClass, PreparedStatement statement, Object param, BoundSql boundSql) throws SQLException, NoSuchFieldException, IllegalAccessException;
}
