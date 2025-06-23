package com.one.framework.sqlnode;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: DynamicContext
 * @Description: SqlNode处理过程中的上下文对象,
 *              SqlNode在解析映射配置文件中的xml节点信息时都使用本类对象进行处理
 * @Author: one
 * @Date: 2022/03/07
 */
public class DynamicContext {
    /**
     * 处理完所有解析得到的sqlNode,最终在sqlBuffer中拼接成一条完成的sql语句
     * 解析同一statement标签下的内容用的是同一个dynamicContext对象
     */
    private StringBuffer sqlBuffer = new StringBuffer();

    /**
     * sqlNode处理过程中需要用到的一些信息, 例如替换 ${}需要用到的入参信息
     */
    private Map<String ,Object> bindings = new HashMap<>();

    /**
     * 构造器
     * @param param 解析sql的入参信息
     */
    public DynamicContext(Object param) {
        // 构造器中先向bindings中添加入参信息, bindings中还会包含其他一些sql解析过程中需要用到的信息
        this.bindings.put("_parameter", param);
    }

    /**
     * 获取sql中缓存的sql语句
     * @return sql语句
     */
    public String getSql() {
        return this.sqlBuffer.toString();
    }

    /**
     * 拼接解析得到的sql片段
     * @param sqlText 需要拼接的sql片段
     */
    public void appendSql(String sqlText) {
        // 空格是sql片段拼接过程中的间隔符
        this.sqlBuffer.append(sqlText).append(" ");
    }

    /**
     * 向bindings中添加需要用到的信息
     * @param name 名称
     * @param value 值
     */
    public void putBinding(String name, Object value) {
        this.bindings.put(name, value);
    }

    public Map<String, Object> getBindings() {
        return this.bindings;
    }
}
