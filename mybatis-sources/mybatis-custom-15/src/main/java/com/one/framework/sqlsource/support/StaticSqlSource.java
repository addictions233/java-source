package com.one.framework.sqlsource.support;


import com.one.framework.sqlsource.BoundSql;
import com.one.framework.sqlsource.SqlSource;

/**
 * @ClassName: StaticSqlSource
 * @Description: 存储DynamicSqlSource和RawSqlSource解析之后的信息
 * @Author: one
 * @Date: 2022/03/07
 */
public class StaticSqlSource implements SqlSource {
    /**
     * 封装了解析之后的可执行sql语句和参数集合List<ParameterMapping>
     */
    private BoundSql boundSql;

    public StaticSqlSource(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public BoundSql getBoundSql(Object param) {
        return this.boundSql;
    }
}
