package com.one.config;

import com.one.framework.sqlsource.SqlSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName: MapperStatement
 * @Description: 对每一个映射配置文件中的CRUD标签封装一个MapperStatement对象,
 *               包含:
 *                    1,唯一标识StatementId,
 *                    2,sql脚本信息 sqlSource
 *                    3,statementType,执行者对象类型
 *                    4,parameterType, sql执行的参数类型
 *                    5,resultType, sql映射的结果类型
 * @Author: one
 * @Date: 2022/02/22
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapperStatement {
    /**
     * namespace + id组成的一条sql语句的唯一标识
     */
    private String statementId;

    /**
     * 封装并解析所有的sqlNode
     */
    private SqlSource sqlSource;

    /**
     * 执行者类型, 默认为预编译执行者对象 preparedStatement
     */
    private String statementType;

    /**
     * 参数类型
     */
    private Class<?> parameterTypeClass;

    /**
     * 映射结果类型
     */
    private Class<?> resultTypeClass;
}
