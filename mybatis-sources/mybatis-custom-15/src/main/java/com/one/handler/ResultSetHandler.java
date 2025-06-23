package com.one.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName: ResultSetHandler
 * @Description: 结果映射的处理器
 * @Author: one
 * @Date: 2022/03/19
 */
public interface ResultSetHandler {
    /**
     * 结果映射
     * @param resultSet 结果集
     * @param resultTypeClass 结果类型
     * @param <T> 结果泛型
     * @return List<T>
     * @throws SQLException 异常
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    <T> List<T> handleResult(ResultSet resultSet, Class<T> resultTypeClass) throws SQLException, IllegalAccessException, InstantiationException;
}
