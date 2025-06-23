package com.one.executor;

import com.one.config.Configuration;
import com.one.config.MapperStatement;

import java.util.List;

/**
 * @InterfaceName: Executor
 * @Description: sql执行器, 定义接口, sqlSession调用查询方法是指派executor执行器来执行真正的sql查询
 * @Author: one
 * @Date: 2022/03/19
 */
public interface Executor {
    /**
     * executor执行器执行sql查询的接口
     * @param <T> 结果泛型
     * @param configuration
     * @param mapperStatement statement标签映射的对象
     * @param param 查询参数
     * @return List<T>
     */
    <T> List<T> doQuery(Configuration configuration, MapperStatement mapperStatement, Object param);
}
