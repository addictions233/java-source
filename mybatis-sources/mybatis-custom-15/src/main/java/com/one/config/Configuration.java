package com.one.config;

import com.one.executor.CachingExecutor;
import com.one.executor.Executor;
import com.one.executor.support.BatchExecutor;
import com.one.executor.support.ReuseExecutor;
import com.one.executor.support.SimpleExecutor;
import com.one.handler.ParameterHandler;
import com.one.handler.ResultSetHandler;
import com.one.handler.StatementHandler;
import com.one.handler.support.DefaultParameterHandler;
import com.one.handler.support.DefaultResultsSetHandler;
import com.one.handler.support.PreparedStatementHandler;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MybatisConfiguration
 * @Description: mybatis的全局配置对象
 *      读取mybatis的核心配置文件内容,并数据源等信息封装到configuration对象中
 * @Author: one
 * @Date: 2022/02/22
 */
public class Configuration {
    /**
     * 数据源信息
     */
    private DataSource dataSource;

    /**
     * 封装所有mapperStatement对象, key是statementId, value是mapperStatement对象
     */
    private Map<String, MapperStatement> mapperStatementMap = new HashMap<>();

    /**
     * 是否开启二级缓存,在配置文件中配置, mybatis默认是不开启二级缓存
     */
    private Boolean userSecondCache = true;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public MapperStatement getMapperStatementById(String statementId) {
        return mapperStatementMap.get(statementId);
    }

    /**
     * 添加statement标签封装的对象
     * @param statementId statement标签的唯一标识
     * @param mapperStatement statement标签封装的对象
     */
    public void putMapperStatement(String statementId, MapperStatement mapperStatement) {
        this.mapperStatementMap.put(statementId, mapperStatement);
    }

    /**
     * 获取执行器对象
     * @param executorType 执行器类型
     * @return 执行器
     */
    public Executor newExecutor(String executorType) {
        executorType = StringUtils.isEmpty(executorType)? "simple" : executorType;
        Executor executor;
        if ("simple".equals(executorType)) { // 简单执行器
            executor = new SimpleExecutor();
        } else if ("reuse".equals(executorType)) { // 可重用执行器
            executor= new ReuseExecutor();
        } else { // 批处理执行器
            executor = new BatchExecutor();
        }
        if (userSecondCache) { // 开启二级缓存
            // 返回缓存执行器
            executor = new CachingExecutor(executor);
        }
        return executor;
    }

    /**
     * 获取statement处理器对象
     * @param statementType statement对象类型 是 prepareStatement, callableStatement....
     * @return Statement
     */
    public StatementHandler newStatementHandler(String statementType) {
        statementType = StringUtils.isEmpty(statementType) ? "prepared" : statementType;
        StatementHandler StatementHandler = null;
        if ("prepared".equals(statementType)) {
            StatementHandler = new PreparedStatementHandler(this);
        } else if ("callabe".equals(statementType)) {
            // TODO 实现不同类型的statementHandler
        }
        return StatementHandler;
    }

    /**
     * 获取参数处理器
     * @return ParameterHandler
     */
    public ParameterHandler newParameterHandler() {
        return new DefaultParameterHandler();
    }

    /**
     * 获取结果处理器
     * @return ResultSetHandler
     */
    public ResultSetHandler newResultSetHandler() {
        return new DefaultResultsSetHandler();
    }
}
