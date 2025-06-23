package com.one.framework.sqlsource;

/**
 * @InterfaceName: SqlSource
 * @Description:   sqlNode: 对XML中的sql脚本信息提供接口处理,并进行封装,解析
 *                 sqlSource:对封装之后的sqlNode提供接口处理,并进行更上层封装,解析
 *                 boundSql: 封装了解析之后的sql语句和对应的参数信息
 * @Author: one
 * @Date: 2022/03/07
 */
public interface SqlSource {
    /**
     * 获取解析之后的sql语句及其参数信息
     *
     * @param param sql中的参数信息
     * @return 封装解析之后的sql语句和对应的参数信息
     */
    BoundSql getBoundSql(Object param);
}
