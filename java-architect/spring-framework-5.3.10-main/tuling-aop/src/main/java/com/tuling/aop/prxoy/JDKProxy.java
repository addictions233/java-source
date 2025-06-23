package com.tuling.aop.prxoy;

import com.tuling.UserInterface;
import com.tuling.UserService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author one
 * @description 使用jdk动态代理
 * @date 2023-7-15
 */
public class JDKProxy {
	public static void main(String[] args) {
		UserService userService = new UserService();
		// 使用jdk动态代理生成代理对象
		Object proxy = Proxy.newProxyInstance(userService.getClass().getClassLoader(), userService.getClass().getInterfaces(), new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Object result;
				if (method.getName().equals("test")) {
					System.out.println("before advice...");
					result = method.invoke(userService, args);
					System.out.println("after advice...");
				} else {
					result = method.invoke(userService, args);
				}
				return result;
			}
		});
		// 将代理对象强转
		UserInterface userInterface = (UserInterface) proxy;
		userInterface.test();
//		userInterface.test2("aaa");
	}
}
