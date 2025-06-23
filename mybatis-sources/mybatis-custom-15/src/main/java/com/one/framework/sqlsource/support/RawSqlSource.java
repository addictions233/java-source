package com.one.framework.sqlsource.support;

import com.one.framework.sqlnode.DynamicContext;
import com.one.framework.sqlnode.SqlNode;
import com.one.framework.sqlsource.BoundSql;
import com.one.framework.sqlsource.SqlSource;
import com.one.utils.GenericTokenParser;
import com.one.utils.ParameterMappingTokenHandler;

/**
 * @ClassName: RawSqlSource
 * @Description: 封装解析出来的sqlNode信息, 包含非动态标签和 #{}的sqlNode对象由本类解析
 *               注意事项: #{} 只需要被解析一次就可以, 预编译直接使用 ? 进行替换
 *                        ${} 要解析多次, 因为 ${}是直接用字符串进行拼接的,有多少个 ${} 就需要
 *                        遍历多少次参数, 然后取到对应的值进行字符串拼接
 * @Author: one
 * @Date: 2022/03/07
 */
public class RawSqlSource implements SqlSource {
    /**
     * 组合复用的设计原则,自己注入自己作为对象的成员变量
     * 封装从sql脚本中解析出来的所有的sqlNode信息, 这里使用的是sqlSource对象,
     * 区别于 DynamicSource中使用 mixedSqlNode
     */
    private SqlSource staticSqlSource;

    /**
     * 构造器, 对要处理的sqlNode进行赋值
     *
     * @param rootSqlNode 封装的sqlNode信息
     */
    public RawSqlSource(SqlNode rootSqlNode) {
        // 1,没有动态标签和${},直接sqlSource中封装的rootSqlNode进行sql脚本的拼接
        // 替换${}需要使用到入参param, 而替换#{}直接用?, 所以这里的param传null
        DynamicContext dynamicContext = new DynamicContext(null);
        rootSqlNode.apply(dynamicContext);
        // 2,获取没有解析 #{}的sql文本信息
        String sqlText = dynamicContext.getSql();
        // 3,使用OGNL对#{}进行解析,并封装#{}中的参数到parameterMapping对象中
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", tokenHandler);
        String sql = tokenParser.parse(sqlText);
        // 4,封装boundSql对象到staticSqlSource对象中
        this.staticSqlSource = new StaticSqlSource(new BoundSql(sql, tokenHandler.getParameterMappings()));
    }

    @Override
    public BoundSql getBoundSql(Object param) {
        return this.staticSqlSource.getBoundSql(param);
    }
}
