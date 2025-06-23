package com.tuling.aop.advisor;

import com.tuling.UserService;
import com.tuling.aop.advice.ZhouyuAroundAdvice;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author one
 * @description 对指定切入点方法添加通知, 形成切面 (切面 = 切入点 + 通知)  advisor = advice + pointcut
 */
public class Main {

	public static void main(String[] args) {
		UserService userService = new UserService();
		// spring所有的动态代理对象都是通过proxyFactory生成的
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(userService);
		// 直接添加切面
		// advisor(切面) = advice(通知) + pointcut(切入点)
		proxyFactory.addAdvisor(new PointcutAdvisor() {
			/**
			 * 设置切入点
			 * @return Pointcut
			 */
			@Override
			public Pointcut getPointcut() {
				return new StaticMethodMatcherPointcut() {
					@Override
					public boolean matches(Method method, Class<?> targetClass) {
						return method.getName().equals("test"); // 只有 test()方法是切点, 形成代理逻辑
					}
				};
			}

			/**
			 * 设置通知
			 * @return advice
			 */
			@Override
			public Advice getAdvice() {
				return new ZhouyuAroundAdvice();
			}

			/**
			 * 这个方法没有实际作用,只在测试类中调用
			 */
			@Override
			public boolean isPerInstance() {
				return false;
			}
		});

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.test2("aa");
	}
}
