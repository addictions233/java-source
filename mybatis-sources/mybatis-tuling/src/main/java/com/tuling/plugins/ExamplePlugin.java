package com.tuling.plugins;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/***
 * @Author 徐庶   QQ:1092002729
 * @Slogan 致敬大师，致敬未来的你
 */
@Intercepts({@Signature(type = Executor.class, method = "query", args = {
        MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class
})})
//@Intercepts({@Signature( type= StatementHandler.class,  method = "update", args ={Statement.class})})
public class ExamplePlugin implements Interceptor {

    //  分页插件   读写分离    Select  增删改
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("代理");
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        // 执行下一个拦截器、直到尽头
        return invocation.proceed();
    }

}