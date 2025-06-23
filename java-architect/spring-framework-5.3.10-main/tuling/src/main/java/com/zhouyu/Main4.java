package com.zhouyu;

import com.zhouyu.service.ProductService;
import com.zhouyu.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author one
 * @description 学习spring推送构造方法
 * @date 2023-12-30
 */
public class Main4 {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		// 可以通过指定getBean()或者BeanDefinition.getConstructorArgumentValues()指定构造参数的入参值, 就会匹配对应参数个数相同的构造方法
		UserService userService = (UserService) context.getBean("userService");
//		UserService userService = (UserService) context.getBean("userService", new OrderService());

		// 如果有多个@Bean注解创建的ProductService对象,会选择参数最多的一个factoryMethod创建bean对象
		ProductService productService = (ProductService) context.getBean("productService");


//		AnnotationConfigApplicationContext context1 = new AnnotationConfigApplicationContext();
//		context1.register(AppConfig.class);
//
//		AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
//		beanDefinition.setBeanClass(UserService.class);
//		// 设置了两个构造参数,就找有两个参数的构造方法
//		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new OrderService());
//		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new OrderService());
		// 使用RuntimeBeanReference会通过beanName找对应的bean对象
//		beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, new RuntimeBeanReference("orderService"));
//		beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
//		context1.registerBeanDefinition("userService", beanDefinition);
//		context1.refresh();
	}
}
