package com.one.executor;

import com.one.config.Configuration;
import com.one.config.MapperStatement;

import java.util.List;

/**
 * @ClassName: CachingExecutor
 * @Description: 缓存执行器,指二级缓存,namespace级别,mybatis默认是不开启的,需要手动开启
 * @Author: one
 * @Date: 2022/03/19
 */
public class CachingExecutor implements Executor {
    /**
     * 基础执行器,能执行真正的sql语句的执行器
     */
    private Executor delegate;

    public CachingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> List<T> doQuery(Configuration configuration, MapperStatement mapperStatement, Object param) {
        // TODO 二级缓存处理,这里暂时不实现, 如果二级缓命中,就直接返回结果
        // 二级缓存没命中,就基础执行器去执行sql查询
        return delegate.doQuery(configuration, mapperStatement, param);
    }
}
