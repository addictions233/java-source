package com.one.framework.sqlnode.support;

import com.one.framework.sqlnode.DynamicContext;
import com.one.framework.sqlnode.SqlNode;
import com.one.utils.OgnlUtils;

/**
 * @ClassName: IfSqlNode
 * @Description: if标签对应的sqlNode:存储<if>标签对应的sql文本信息
 * @Author: one
 * @Date: 2022/03/07
 */
public class IfSqlNode implements SqlNode {

    /**
     * <if>标签中的判断语句, 即test属性的属性值
     */
    private String test;

    /**
     * <if>标签的子项封装成的sqlNode对象, <if>标准子项可以嵌套文本内容或者其他动态标签
     */
    private SqlNode mixedSqlNode;

    public IfSqlNode(String test, SqlNode mixedSqlNode) {
        this.test = test;
        this.mixedSqlNode = mixedSqlNode;
    }

    @Override
    public void apply(DynamicContext context) {
        // #{}, ${}, <if>中的test判断表达式都是由OGNL解析的
        Object param = context.getBindings().get("_parameter");
        boolean flag = OgnlUtils.evaluateBoolean(test, param);
        if (flag) { // 只有当<if>标签的test判断表示为true时,才解析<if>标签中的内容
            mixedSqlNode.apply(context);
        }

    }
}
