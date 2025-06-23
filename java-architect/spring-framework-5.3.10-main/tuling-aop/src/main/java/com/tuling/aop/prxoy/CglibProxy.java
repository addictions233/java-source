package com.tuling.aop.prxoy;

import com.tuling.UserInterface;
import com.tuling.UserService;
import org.springframework.cglib.proxy.*;

import java.lang.reflect.Method;

/**
 * Spring引入了cglib实现动态代理的技术, 完全拷贝了一份cglib的依赖包到spring项目中,
 * 这样spring项目不会因为cglib jar包
 * 的更新而需要更新
 *
 * @author one
 */
public class CglibProxy {

	public static void main(String[] args) {
		//target目标对象(被代理对象)
		UserService target = new UserService();

		Enhancer enhancer = new Enhancer();
		// cglib如果设置了supperClass和interfaces,那么生成的代理对象就继承了supperClass类,并实现interFaces接口
		enhancer.setSuperclass(UserService.class);
		enhancer.setInterfaces(new Class[] {UserInterface.class});
		enhancer.setCallbacks(new Callback[]{new MethodInterceptor() {
			/**
			 * 设置通知, 可以设置多个通知
			 */
			@Override
			public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
				System.out.println("before advice...");
				// 调用被代理对象的原始切面方法
//				Object result = method.invoke(target, args);
				// 调用代理对象的super就是被代理对象
//				Object result = methodProxy.invokeSuper(proxy, args);
				// 还是调用被代理对象的原始切面方法
				Object result = methodProxy.invoke(target, args);
				// 这种方法会出现死循环调用,会发生intercept()方法循环调用
//				Object result = method.invoke(proxy, args);
				System.out.println("after advice...");
				return result;
			}
		},  NoOp.INSTANCE});
		enhancer.setCallbackFilter(new CallbackFilter() {
			/**
			 * 设置切入点, 需要通知增强的方法
			 *
			 * @param method 连接点方法
			 * @return 返回的是callBacks的数组下标
			 */
			@Override
			public int accept(Method method) {
				if (method.getName().equals("test")) {
					return 0; // 返回的是callBacks的数组下标
				} else {
					return 1;
				}
			}
		});
		// 使用cglib创建proxy代理对象
		UserService userService = (UserService) enhancer.create();
		// proxy代理对象中会重写target被代理对象的test()方法, 代理对象中的test()方法中
		// 实际调用的是intercept()方法中的内容
//		userService.test();
		// test2方法不满足切入点要求,使用下标为1的拦截器, 即啥都不做
		userService.test2("zhouyu");

//		UserInterface userInterface = (UserInterface) userService;
//		System.out.println(userInterface);
	}
}
