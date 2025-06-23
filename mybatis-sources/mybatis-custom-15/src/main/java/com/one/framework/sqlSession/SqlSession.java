package com.one.framework.sqlSession;

import java.util.List;

/**
 * @InterfaceName: SqlSession
 * @Description: 执行sql语句的根接口, mybatis对外最核心的接口
 * @Author: one
 * @Date: 2022/03/16
 */
public interface SqlSession {
    /**
     * 查询多个
     *
     * @param statementId statement的唯一标识
     * @param param 查询参数
     * @param <T> 结果泛型
     * @return List<T>
     */
    <T> List<T> selectList(String statementId, Object param);

    /**
     * 查询单个
     *
     * @param statementId statement唯一标识
     * @param param 查询参数
     * @param <T> 结果泛型
     * @return T
     */
    <T> T selectOne(String statementId, Object param);
}
