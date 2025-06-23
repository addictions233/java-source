package com.tuling.aop.advisor;

import com.tuling.aop.advice.ZhouyuBeforeAdvice;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author one
 * @description 自定义pointcutAdvisor切面, 所有的advisor也会转换为methodInterceptor
 * @date 2023-7-16
 */
public class ZhouyuPointcutAdvisor implements PointcutAdvisor {
	/**
	 * 定义通知
	 * @return Advice
	 */
	@Override
	public Advice getAdvice() {
		return new ZhouyuBeforeAdvice();
	}

	/**
	 * 定义切入点
	 *
	 * @return Pointcut
	 */
	@Override
	public Pointcut getPointcut() {
		return new StaticMethodMatcherPointcut() {
			@Override
			public boolean matches(Method method, Class<?> targetClass) {
				return method.getName().equals("test");
			}
		};
	}

	/**
	 *
	 * @return
	 */
	@Override
	public boolean isPerInstance() {
		return false;
	}


}
