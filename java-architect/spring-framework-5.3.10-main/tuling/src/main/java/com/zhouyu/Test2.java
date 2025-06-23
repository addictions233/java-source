package com.zhouyu;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.ClassUtils;

/**
 * @author one
 * @description 学习Bean生命周期
 * @date 2023-4-6
 */
public class Test2 {
	public static void main(String[] args) {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();// 当前线程的类加载器
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
		// 创建bean对象之前得先加载bean的字节码文件
		ClassLoader beanClassLoader = applicationContext.getBeanFactory().getBeanClassLoader();
		// 默认的的classLoader(优先使用当前线程的类加载器appClassLoader), Tomcat中每个类都有自定义的类加载器, 也可能是bootStrapClassLoader
		applicationContext.getBeanFactory().setBeanClassLoader(ClassUtils.getDefaultClassLoader());
		System.out.println(applicationContext.getBean("userService"));;
	}
}
