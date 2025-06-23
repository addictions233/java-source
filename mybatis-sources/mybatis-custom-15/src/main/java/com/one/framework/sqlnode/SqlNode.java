package com.one.framework.sqlnode;

/**
 * @InterfaceName: SqlNode
 * @Description: 对映射配置文件XML中的SQL脚本信息进行封装并解析
 * @Author: one
 * @Date: 2022/03/07
 */
public interface SqlNode {
    /**
     * 给sqlNode对象指定处理过程中的上下文对象
     *
     * @param context sqlNode的处理过程中的上下文对象
     */
    void apply(DynamicContext context);
}
