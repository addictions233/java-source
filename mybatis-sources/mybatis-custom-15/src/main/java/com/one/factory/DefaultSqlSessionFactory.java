package com.one.factory;

import com.one.config.Configuration;
import com.one.framework.sqlSession.DefaultSqlSession;
import com.one.framework.sqlSession.SqlSession;

/**
 * @ClassName: DefaultSqlSessionFactory
 * @Description: sqlSessionFactory的默认实现
 * @Author: one
 * @Date: 2022/03/16
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    /**
     * SqlSessionFactory对象是用来构建
     */
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
