package com.zhouyu;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author one
 * @description 学习包扫描生成BeanDefinition
 * @date 2023-2-22
 */
public class Main2 {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
		
//		System.out.println("=======userServiceFactoryBean=====");
//		System.out.println(applicationContext.getBean("userServiceFactoryBean")); // 实际获取的bean对象是userService

		// IOC默认是不会扫描抽象类创建bean对象的,  除非该抽象类中有@Lookup注解修饰的方法
//		AbstractService abstractService = applicationContext.getBean(AbstractService.class);
//		abstractService.print();
//		abstractService.print2();
//		abstractService.print();
//		abstractService.print2();
//		abstractService.print();
//		abstractService.print2();

		// 如果bean对象的类名第一个字符和第二个字符都是大写, 那么直接把该类名作为bean对象名称(区别于类名首字母小写的生成bean对象名称方式)
//		System.out.println(applicationContext.getBean("ABtest"));
		Object bean1 = applicationContext.getBean("userServiceFactoryBean");
		System.out.println(bean1);
		Object bean2 = applicationContext.getBean("&userServiceFactoryBean");
		System.out.println(bean2);
		applicationContext.close();

	}
}
