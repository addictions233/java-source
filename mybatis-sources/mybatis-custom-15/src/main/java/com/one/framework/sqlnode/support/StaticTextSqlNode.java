package com.one.framework.sqlnode.support;

import com.one.framework.sqlnode.DynamicContext;
import com.one.framework.sqlnode.SqlNode;

/**
 * @ClassName: StaticTextSqlNode
 * @Description: 不包含${}文本内容对应的sqlNode:存储没有${}的sql文本信息(可以带有#{})
 * @Author: one
 * @Date: 2022/03/07
 */
public class StaticTextSqlNode implements SqlNode {

    private String sqlText;

    public StaticTextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }

    @Override
    public void apply(DynamicContext context) {
        context.appendSql(sqlText);
    }
}
