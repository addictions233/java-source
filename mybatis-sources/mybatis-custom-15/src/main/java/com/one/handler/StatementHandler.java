package com.one.handler;

import com.one.config.MapperStatement;
import com.one.framework.sqlsource.BoundSql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @InterfaceName: StatementHandler
 * @Description: statement处理器: 包括 statement, preparedStatement, callableStatement
 * @Author: one
 * @Date: 2022/03/19
 */
public interface StatementHandler {
    /**
     * 获取statement执行者对象
     * @param connection 连接对象
     * @param sql  可执行sql
     * @return Statement
     */
    Statement prepare(Connection connection, String sql) throws SQLException;

    /**
     * 进行sql的参数处理
     *
     * @param statement 执行者对象
     * @param mapperStatement 封装了statement标签
     * @param param 查询参数
     * @param boundSql 封装了sql和参数信息
     */
    void parameterize(Statement statement, MapperStatement mapperStatement, Object param, BoundSql boundSql);

    /**
     * 进行查询的结果映射
     * @param statement 执行者对象
     * @param mapperStatement 封装了statement标签
     * @param <T> 结果泛型
     * @return List<T>
     */
    <T> List<T> doQuery(Statement statement, MapperStatement mapperStatement);
}
