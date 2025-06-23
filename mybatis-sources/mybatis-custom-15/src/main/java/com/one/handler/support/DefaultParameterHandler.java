package com.one.handler.support;

import com.one.framework.sqlsource.BoundSql;
import com.one.framework.sqlsource.ParameterMapping;
import com.one.handler.ParameterHandler;
import com.one.utils.SimpleTypeRegistry;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DefaultParameterHandler
 * @Description: 默认的参数处理器实现类
 * @Author: one
 * @Date: 2022/03/19
 */
public class DefaultParameterHandler implements ParameterHandler {
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
    @Override
    public void setParameter(Class<?> parameterTypeClass, PreparedStatement statement, Object param, BoundSql boundSql) throws SQLException, NoSuchFieldException, IllegalAccessException {
        if (SimpleTypeRegistry.isSimpleType(param.getClass())) { // 参数为基本数据类型
            statement.setObject(1, param);
        } else if (param instanceof Map) { // 参数为Map类型
            Map<String, Object> paramMap = (Map<String, Object>)param;
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                String name = parameterMapping.getName();
                statement.setObject(i+1, paramMap.get(name));
            }
        } else { // 参数为对象类型
            Class<?> paramClass = param.getClass();
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                String name = parameterMapping.getName();
                Field field = paramClass.getDeclaredField(name);
                field.setAccessible(true);
                statement.setObject(i+1, field.get(param));
            }
        }
    }
}
