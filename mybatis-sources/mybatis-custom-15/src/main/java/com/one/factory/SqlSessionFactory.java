package com.one.factory;

import com.one.framework.sqlSession.SqlSession;

/**
 * @InterfaceName: SqlSessionFactory
 * @Description: 使用建造者设计模式构建sqlSession对象
 * @Author: one
 * @Date: 2022/03/16
 */
public interface SqlSessionFactory {
    /**
     * 使用建造者设计模式构建sqlSession对象
     * @return sqlSession
     */
    SqlSession openSession();
}
