package com.one.framework.sqlnode.support;

import com.one.framework.sqlnode.DynamicContext;
import com.one.framework.sqlnode.SqlNode;

import java.util.List;

/**
 * @ClassName: MixedSqlNode
 * @Description: 同一级别的多个sqlNode组成的集合:存储同一级别的sql文本信息
 * @Author: one
 * @Date: 2022/03/07
 */
public class MixedSqlNode implements SqlNode {

    /**
     * 同一级别的sqlNode有多个,用list集合存储
     */
    private List<SqlNode> sqlNodes;

    public MixedSqlNode(List<SqlNode> sqlNodes) {
        this.sqlNodes = sqlNodes;
    }

    @Override
    public void apply(DynamicContext context) {
        for (SqlNode sqlNode : sqlNodes) {
            sqlNode.apply(context);
        }

    }
}
