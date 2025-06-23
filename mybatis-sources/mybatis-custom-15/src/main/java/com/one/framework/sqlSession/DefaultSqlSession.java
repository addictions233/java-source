package com.one.framework.sqlSession;

import com.one.config.Configuration;
import com.one.config.MapperStatement;
import com.one.executor.Executor;

import java.util.List;

/**
 * @ClassName: DefaultSqlSession
 * @Description: 执行查询的sqlSession接口的默认实现类
 * @Author: one
 * @Date: 2022/03/16
 */
public class DefaultSqlSession implements SqlSession {
    /**
     * sqlSession接口的实现类对象是唯一对外暴露用来执行sql语句的对象
     * 所以必须要有configuration属性值,才能执行sql语句
     */
    private Configuration configuration;

    /**
     * 构造器
     * @param configuration 封装了dataSource和mapperStatement
     */
    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 查询多个, sqlSession的执行查询真正是委派为executor执行器
     * @param statementId statement的唯一标识
     * @param param 查询参数
     * @param <T> 结果泛型
     * @return List<T>
     */
    @Override
    public <T> List<T> selectList(String statementId, Object param) {
        // 获取statement标签内容
        MapperStatement mapperStatement = configuration.getMapperStatementById(statementId);
        // 获取sql执行器对象, 使用简单类型执行器
        Executor executor = configuration.newExecutor("simple");
        // 由sql执行器执行mapperStatement对象中封装的sql语句,并返回结果
        return executor.doQuery(configuration,mapperStatement,param);
    }

    /**
     * 查询单个
     * @param statementId statement唯一标识
     * @param param 查询参数
     * @param <T> 结果泛型
     * @return T
     */
    @Override
    public <T> T selectOne(String statementId, Object param) {
        List<T> results = this.selectList(statementId, param);
        if(results != null && results.size() == 1) {
            return results.get(0);
        } else {
            // TODO 查询结果不符合预期,抛出异常
            throw new RuntimeException();
        }
    }
}
