package com.one.handler.support;

import com.one.handler.ResultSetHandler;
import com.one.pojo.SexEnum;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DefaultResultSetHandler
 * @Description: 默认的结果处理器实现类
 * @Author: one
 * @Date: 2022/03/19
 */
public class DefaultResultsSetHandler implements ResultSetHandler {
    /**
     * 结果映射
     * @param resultSet 结果集
     * @param resultTypeClass 结果类型
     * @param <T> 结果泛型
     * @return List<T>
     * @throws SQLException 异常
     */
    @Override
    public <T> List<T> handleResult(ResultSet resultSet, Class<T> resultTypeClass) throws SQLException, IllegalAccessException, InstantiationException {
        List<T> resultList = new ArrayList<>();
        while (resultSet.next()) {
            T result = resultTypeClass.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnName = metaData.getColumnName(i+1);
                Field field = null;
                try {
                    field = resultTypeClass.getDeclaredField(columnName);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if (field == null) { // 映射结果类中并没有sql查询结果中的对应字段
                    continue;
                }
                field.setAccessible(true);
                if ("sex".equals(columnName)) {
                    field.set(result, SexEnum.getName(resultSet.getInt(i)));
                } else {
                    field.set(result, resultSet.getObject(columnName));
                }
            }
            resultList.add(result);
        }
        return resultList.size() == 0 ? null : resultList;
    }
}
