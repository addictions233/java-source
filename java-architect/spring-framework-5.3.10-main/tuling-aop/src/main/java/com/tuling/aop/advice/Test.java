package com.tuling.aop.advice;

import com.tuling.UserInterface;
import com.tuling.UserService;
import org.springframework.aop.framework.ProxyFactory;

/**
 * @author one
 * @description 测试类
 * @date 2023-7-8
 */
public class Test {

	public static void main(String[] args) {
		UserService userService = new UserService();
		// spring所有的动态代理对象都是通过spring生成的
		ProxyFactory proxyFactory = new ProxyFactory();

		proxyFactory.setTarget(userService);
		// 添加通知,对所有的连接点joint point生效
		proxyFactory.addAdvice(new ZhouyuAroundAdvice());
		proxyFactory.addAdvice(new ZhouyuBeforeAdvice());
		// 如果proxyFactory设置了interFaces属性就是用的jdk动态代理
		proxyFactory.addInterface(UserInterface.class);
		// 但是如果proxyFactory设置了proxyTargetClass为true,即使添加了接口也是用cglib动态代理
		proxyFactory.setProxyTargetClass(true);
		proxyFactory.setExposeProxy(true);
		// 如果用的jdk动态代理, 将代理对象转换为UserService就会报类型转换异常
//		UserService proxy = (UserService) proxyFactory.getProxy();
		// 将代理对象进行类型转换
		UserInterface userInterface = (UserInterface) proxyFactory.getProxy();
//		proxy.test();
		userInterface.test();
	}
}
