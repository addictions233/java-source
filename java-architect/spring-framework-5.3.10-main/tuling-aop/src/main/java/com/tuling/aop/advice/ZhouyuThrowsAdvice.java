package com.tuling.aop.advice;

import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;

/**
 * @author 周瑜
 */
public class ZhouyuThrowsAdvice implements ThrowsAdvice {

	/**
	 * 抛异常通知
	 *
	 * @param method
	 * @param args
	 * @param target
	 * @param ex 只有抛出指定异常才能执行代理逻辑
	 */
	public void afterThrowing(Method method, Object[] args, Object target, NullPointerException ex) {
		System.out.println("方法抛出异常后执行");
	}

}
