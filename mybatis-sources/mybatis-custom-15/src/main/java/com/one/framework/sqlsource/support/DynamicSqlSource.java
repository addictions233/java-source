package com.one.framework.sqlsource.support;

import com.one.framework.sqlnode.DynamicContext;
import com.one.framework.sqlnode.SqlNode;
import com.one.framework.sqlsource.BoundSql;
import com.one.framework.sqlsource.SqlSource;
import com.one.utils.GenericTokenParser;
import com.one.utils.ParameterMappingTokenHandler;

/**
 * @ClassName: DynamicSqlSource
 * @Description: 封装解析出来的sqlNode信息, 包含动态标签和 ${}的sqlNode对象由本类解析
 * @Author: one
 * @Date: 2022/03/07
 */
public class DynamicSqlSource implements SqlSource {
    /**
     * 封装从sql脚本中解析出来的所有的sqlNode信息
     */
    private SqlNode rootSqlNode;

    /**
     * 构造器: 对要处理的sqlNode进行赋值, 封装过程
     * @param sqlNode 封装的sqlNode信息
     */
    public DynamicSqlSource(SqlNode sqlNode) {
        this.rootSqlNode = sqlNode;
    }

    /**
     * 解析sqlNode中封装的sql语句, 解析过程
     * @param param sql中的参数信息
     * @return 解析出来的sql
     */
    @Override
    public BoundSql getBoundSql(Object param) {
        // 1,解析rootSqlNode对象, 并对 ${}和动态标签进行处理
        // dynamicContext对象中封装了拼接sql用的StringBuilder, 参数信息
        DynamicContext dynamicContext = new DynamicContext(param);
        // rootSqlNode中存放了待解析的mixSqlNode
        // 而dynamicContext对象中存放了解析需要用到的param,以及解析后存放的sql容器 StringBuffer
        // 解析rootSqlNode对象中封装的动态标签${}
        rootSqlNode.apply(dynamicContext);
        // 此时获取的sqlContext只处理了动态标签和${}, 还有#{}需要用OGNL解析处理
        //  2,使用OGNL对sqlContext中包含的#{}进行解析处理
        String sqlContext = dynamicContext.getSql();
        // 处理带有#{}的sql文本解析器, 解析#{}时将其内容封装成parameterMapping的对象集合
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{","}",tokenHandler);
        String sql = tokenParser.parse(sqlContext);
        // 3,将最终解析可执行sql语句和 List<ParameterMapping>对象封装成boundSql返回
        return new BoundSql(sql, tokenHandler.getParameterMappings());
    }
}
