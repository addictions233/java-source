package com.one.handler.support;

import com.one.config.Configuration;
import com.one.config.MapperStatement;
import com.one.framework.sqlsource.BoundSql;
import com.one.handler.ParameterHandler;
import com.one.handler.ResultSetHandler;
import com.one.handler.StatementHandler;

import java.sql.*;
import java.util.List;

/**
 * @ClassName: PreparedStatementHandler
 * @Description: statementHandler的实现类对象
 * @Author: one
 * @Date: 2022/03/19
 */
public class PreparedStatementHandler implements StatementHandler {
    /**
     * 参数处理器
     */
    private ParameterHandler parameterHandler;

    /**
     * 结果处理器
     */
    private ResultSetHandler resultSetHandler;

    public PreparedStatementHandler(Configuration configuration) {
        this.parameterHandler = configuration.newParameterHandler();
        this.resultSetHandler = configuration.newResultSetHandler();
    }

    /**
     * 通过connection连接对象获取statement对象
     *
     * @param connection 连接对象
     * @param sql        可执行sql
     * @return statement
     */
    @Override
    public Statement prepare(Connection connection, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    /**
     * 使用statement对象进行sql的参数处理
     *
     * @param statement       执行者对象
     * @param mapperStatement 封装了statement标签
     * @param param           查询参数
     * @param boundSql        封装了sql和参数信息
     */
    @Override
    public void parameterize(Statement statement, MapperStatement mapperStatement, Object param, BoundSql boundSql) {
        Class<?> parameterTypeClass = mapperStatement.getParameterTypeClass();
        try {
            parameterHandler.setParameter(parameterTypeClass, (PreparedStatement)statement, param, boundSql);
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        doQuery(statement, mapperStatement);
    }

    @Override
    public <T> List<T> doQuery(Statement statement, MapperStatement mapperStatement) {
        PreparedStatement preparedStatement = (PreparedStatement)statement;
        Class<T> resultTypeClass = (Class<T>) mapperStatement.getResultTypeClass();
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSetHandler.handleResult(resultSet,resultTypeClass);
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
