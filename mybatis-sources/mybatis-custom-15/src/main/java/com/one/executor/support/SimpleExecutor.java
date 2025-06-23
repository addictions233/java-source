package com.one.executor.support;

import com.one.config.Configuration;
import com.one.config.MapperStatement;
import com.one.executor.BaseExecutor;
import com.one.framework.sqlsource.BoundSql;
import com.one.handler.StatementHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @ClassName: SimpleExecutor
 * @Description: Statement, preparedStatement, callableStatement执行sql语句时都是使用这个执行器
 * @Author: one
 * @Date: 2022/03/19
 */
public class SimpleExecutor extends BaseExecutor {
    @Override
    public <T> List<T> queryFromDataSource(Configuration configuration,MapperStatement mapperStatement, Object param, BoundSql boundSql) {
        // 1,获取connection连接对象
        Connection connection = getConnection(configuration);
        // 2,获取预编译执行者对象
        String statementType = mapperStatement.getStatementType();
        StatementHandler statementHandler = configuration.newStatementHandler(statementType);
        // 获取可执行的sql
        String sql = boundSql.getSql();
        // 通过connection连接对象获取preparedStatement预编译执行者对象
        Statement statement = null;
        try {
            statement = statementHandler.prepare(connection, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 使用statement对象进行查询的sql进行参数处理
        statementHandler.parameterize(statement, mapperStatement, param, boundSql);
        // 使用statement对象查询的结果进行映射
        List<T> results = statementHandler.doQuery(statement,mapperStatement);
        return results;
    }

    private Connection getConnection(Configuration configuration) {
        Connection connection = null;
        // 获取dataSource数据源
        DataSource dataSource = configuration.getDataSource();
        try {
            // 获取connection连接对象
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
