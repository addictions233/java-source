package com.tuling.aop.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 注解 @Aspect和@Before等都是 aspectj(编译期织入代理逻辑实现aop的技术)的注解,
 * Spring中的动态代理只是借用了aspectj的这些注解,
 * 该注解的底层实现aop的还是spring自己的动态代理技术(运行期织入), 不是aspectj的编译期织入原理
 */
@Aspect
@Component
public class ZhouyuAspect {
	/**
	 * 定义切入点
	 */
	@Pointcut("execution(public void com.tuling.UserService.test())")
	public void a(){

	}

	/**
	 * 定义前置通知
	 * @param joinPoint 连接点 (target目标对象中可以被增强的方法)
	 */
	@Before("a()")
	public void zhouyuBefore(JoinPoint joinPoint) {
		System.out.println("zhouyuBefore advice..");
	}


}
