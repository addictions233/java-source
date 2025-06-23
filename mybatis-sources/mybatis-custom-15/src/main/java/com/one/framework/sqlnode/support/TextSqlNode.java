package com.one.framework.sqlnode.support;

import com.one.framework.sqlnode.DynamicContext;
import com.one.framework.sqlnode.SqlNode;
import com.one.utils.GenericTokenParser;
import com.one.utils.OgnlUtils;
import com.one.utils.SimpleTypeRegistry;
import com.one.utils.TokenHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: TextSqlNode
 * @Description: 包含${}文本内容对应的sqlNode: 存储带有${}的sql文本信息
 * @Author: one
 * @Date: 2022/03/07
 */
public class TextSqlNode implements SqlNode {
    /**
     * sqlNode中的文本内容
     */
    private String sqlText;

    /**
     * 构造器
     * @param sqlText sqlNode中的文本信息
     */
    public TextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }

    /**
     * 判断sqlNode中的文本内容是否包含 ${}符号
     * @return
     */
    public boolean isDynamic() {
        if (StringUtils.isEmpty(sqlText)) {
            return false;
        }
        return sqlText.contains("${");
    }

    /**
     * 解析sqlNode对象中封装的sqlText, context中封装了解析sqlText需要的param入参,
     * 以及解析之后的sql容器 stringBuffer
     *
     * @param context sqlNode的处理过程中的上下文对象
     */
    @Override
    public void apply(DynamicContext context) {
        // 获取参数用来解析替换${}
        // 创建token处理器对象
        BindSqlTokenHandler tokenHandler = new BindSqlTokenHandler(context);
        GenericTokenParser tokenParser = new GenericTokenParser("${","}", tokenHandler);
        // 半成品的可执行sql, 解析了${}, 可能还存在#{}
        String sql = tokenParser.parse(sqlText);
        // 最终的可执行sql是封装在context对象中的stringBuffer中
        context.appendSql(sql);
    }

    /**
     * 内部类: 用来处理sqlNode中封装的${}
     */
    private class BindSqlTokenHandler implements TokenHandler {
        /**
         * 参数,处理${}需要用参数中的内容进行替换
         */
        private DynamicContext context;

        private BindSqlTokenHandler(DynamicContext context) {
            this.context = context;
        }

        /**
         * 处理 ${}
         * @param content ${}中的内容
         * @return 替换${}的字符串
         */
        @Override
        public String handleToken(String content) {
            Object param = context.getBindings().get("_parameter");
            if (param == null) { // 参数为null
                return StringUtils.EMPTY;
            } else if (SimpleTypeRegistry.isSimpleType(param.getClass())) { // 简单数据类型
                return param.toString();
            }
            // 如果param为Map类型或者对象类型,就用OGNL来获取对应的值
            Object value = OgnlUtils.getValue(content, param);
            return value  == null ? StringUtils.EMPTY : value.toString();
        }
    }
}
