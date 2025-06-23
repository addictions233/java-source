package com.one.executor.support;

import com.one.config.Configuration;
import com.one.config.MapperStatement;
import com.one.executor.BaseExecutor;
import com.one.framework.sqlsource.BoundSql;

import java.util.List;

/**
 * @ClassName: BatchExecutor
 * @Description: 批处理执行器
 * @Author: one
 * @Date: 2022/03/19
 */
public class BatchExecutor extends BaseExecutor {
    @Override
    public <T> List<T> queryFromDataSource(Configuration configuration,MapperStatement mapperStatement, Object param, BoundSql boundSql) {
        return null;
    }
}
