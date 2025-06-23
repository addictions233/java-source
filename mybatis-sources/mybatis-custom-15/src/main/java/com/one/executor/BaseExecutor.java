package com.one.executor;

import com.one.config.Configuration;
import com.one.config.MapperStatement;
import com.one.framework.sqlsource.BoundSql;
import com.one.framework.sqlsource.SqlSource;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: BaseExecutor
 * @Description: 基础执行器,一级缓存,sqlSession级别, 二级缓存没命中,就得调用一级缓存执行
 *      抽象类: 子类必须实现queryFromDataSource方法, 有三个子类
 *          1, SimpleExecutor  Statement, PreparedStatement, CallableStatement都使用此执行器
 *          2, ReuseExecutor   可重用执行器
 *          3, BatchExecutor   批处理执行器
 * @Author: one
 * @Date: 2022/03/19
 */
public abstract class BaseExecutor implements Executor {
    private ConcurrentHashMap<String, Object> perpetualCache = new ConcurrentHashMap<>();

    @Override
    public <T> List<T> doQuery(Configuration configuration, MapperStatement mapperStatement, Object param) {
        SqlSource sqlSource = mapperStatement.getSqlSource();
        // 调用替换${}, #{}, 处理动态标签的方法
        BoundSql boundSql = sqlSource.getBoundSql(param);
        // 得到可执行的sql
        String sql = boundSql.getSql();
        // 一级缓存处理, 用map存储查询结果
        String cacheKey = generateCacheKey(sql, param);
        Object cacheResult = perpetualCache.get(cacheKey);
        if (cacheResult != null) {
            // 一级缓存命中,直接返回结果
            return (List<T>)cacheResult;
        }
        // 一级没有命中,就将执行sql语句
        List<T> results = queryFromDataSource(configuration, mapperStatement, param, boundSql);
        // 将查询结果放入一级缓存中
        perpetualCache.put(cacheKey, results);
        return results;
    }

    /**
     * 只有满足多个条件相同,mybatis才会认为两次查询一样, 包括statementId, sql, 参数param等一致的情况
     * @param sql 查询sql
     * @param param 查询param
     * @return 缓存结果在perpetualCache中缓存key
     */
    private String generateCacheKey(String sql, Object param) {
        // 一级缓存命中的条件很严格,实现也比较复杂,这里不做具体实现
        return sql;
    }

    /**
     * 使用执行器执行sql语句,获取查询结果
     * @param configuration 全局配置对象
     * @param mapperStatement 封装了statement标签的对象
     * @param param 查询参数
     * @param boundSql sql语句和参数信息
     * @param <T> 结果泛型
     * @return List<T>
     */
    public abstract <T> List<T> queryFromDataSource(Configuration configuration,MapperStatement mapperStatement, Object param, BoundSql boundSql);
}
