package com.zhouyu.configuration;

import com.zhouyu.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author one
 * @description TODO
 * @date 2024-3-17
 */
public class Main {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		// 如果proxyBeanMethods = false, 那么@Bean方法不会被代理, 每次调用执行完整的方法, 都会创建一个新的对象
		// 如果proxyBeanMethods = true, 那么@Bean方法会被代理, 每次都是直接从IOC容器中获取对象,如果IOC容器中没有才会创建Bean对象
		// 如果Configuration配置类中的多个@Bean方法之间有依赖关系, 那么proxyBeanMethods = false会导致依赖关系失效
		// 如果Configuration配置类中的多个@Bean方法之间没有依赖, 可以设置为lite模式, 这样spring的启动速度比较快
		UserConfiguration userConfiguration = context.getBean(UserConfiguration.class);
		User user1 = userConfiguration.user();
		User user2 = userConfiguration.user();
		System.out.println(user1 == user2);
	}
}
