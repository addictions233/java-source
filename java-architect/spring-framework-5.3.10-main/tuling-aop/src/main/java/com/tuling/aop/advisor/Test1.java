package com.tuling.aop.advisor;

import com.tuling.UserService;
import org.springframework.aop.framework.ProxyFactory;

/**
 * @author one
 * @description 分析ProxyFactory的底层源码实现
 * @date 2023-7-16
 */
public class Test1 {
	public static void main(String[] args) {
		UserService userService = new UserService();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(userService);
//		proxyFactory.addInterface(UserInterface.class); // 这种情况hasNoUserSuppliedProxyInterfaces(config), 使用jdk动态代理
//		proxyFactory.setTargetClass(UserInterface.class); // 这种情况targetClass.isInterface()返回true
		proxyFactory.addAdvisor(new ZhouyuPointcutAdvisor());

//		proxyFactory.addAdvisor(new ZhouyuOriginPointcutAdvisor());

		proxyFactory.setExposeProxy(true); // 将当前代理对象放入ThreadLocal中,可以通过 AopContext.currentProxy() 获取到当前代理对象
//		proxyFactory.setProxyTargetClass(true); // 使用cglib动态代理

		UserService proxy = (UserService) proxyFactory.getProxy();
//		UserInterface proxy = (UserInterface) proxyFactory.getProxy();
		proxy.test();
	}
}
