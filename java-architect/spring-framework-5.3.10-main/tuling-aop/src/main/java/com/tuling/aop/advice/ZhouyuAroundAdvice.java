package com.tuling.aop.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * 所有的advice或者advisor都会转换为MethodInterceptor: advise + pointcut = advisor, advisor又封装成了对应的MethodInterceptor
 * @author 周瑜
 */
@Component
public class ZhouyuAroundAdvice implements MethodInterceptor {

	@Nullable
	@Override
	public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
		System.out.println("方法执行Around前");
		Object proceed = invocation.proceed(); // 调用被代理对象切面方法
		System.out.println("方法执行Around后");
		return proceed;
	}
}
