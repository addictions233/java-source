package com.tuling.aop.prxoy;

import com.tuling.UserService;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Method;

/**
 * @author one
 * @description spring封装了aop, 直接使用proxyFactory生成代理逻辑, 使得我们不用关系底层是用cglib还是jdk proxy
 * @date 2024-1-3
 */
public class SpringProxyFactory {

	public static void main(String[] args) {
		UserService userService = new UserService();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(userService);
		proxyFactory.addAdvice(new MethodBeforeAdvice() {
			@Override
			public void before(Method method, Object[] args, Object target) throws Throwable {
				System.out.println("before advice..");
//				method.invoke(target,args); // 会自动执行被代理方法,所以我们不用重写
			}
		});

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.test();
	}
}
