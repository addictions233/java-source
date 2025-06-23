package com.zhouyu;

import com.zhouyu.service.UserService;
import com.zhouyu.utils.ApplicationContextUtil;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.Resource;

/**
 * 学习Bean生命周期
 */
public class Test {

	public static void main(String[] args) {

		// 创建一个Spring容器, 非懒加载的单例bean在spring容器启动时加载
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//		AnnotationConfigApplicationContext applicationContext2 = new AnnotationConfigApplicationContext();
//		applicationContext2.register(AppConfig.class); // 读取配置类
//		applicationContext2.scan("com.zhouyu"); // 直接进行包扫描
//		applicationContext2.refresh(); // 不刷新会报错

		UserService userService = (UserService) applicationContext.getBean("userService");
		userService.test();
		// IOC创建bean对象之前,都是对每个bean先创建BeanDefinition对象
		AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
		beanDefinition.setBeanClass(UserService.class);
		beanDefinition.setScope("prototype");
		applicationContext.registerBeanDefinition("userService", beanDefinition);
		System.out.println(applicationContext.getBean("userService"));

		// 将创建的BeanDefinition对象放入beanFactory,相当于创建了一个bean对象
		AnnotatedBeanDefinitionReader annotatedBeanDefinitionReader = new AnnotatedBeanDefinitionReader(applicationContext);
		annotatedBeanDefinitionReader.registerBean(UserService.class);
		System.out.println(applicationContext.getBean("userService"));

		ClassPathBeanDefinitionScanner beanDefinitionScanner = new ClassPathBeanDefinitionScanner(applicationContext);
		beanDefinitionScanner.scan("com.zhouyu");

		// spring容器中功能强大的一个beanFactory, applicationContext继承自DefaultListableBeanFactory
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerBeanDefinition("userService", beanDefinition);

		// 国际化
//		System.out.println(applicationContext.getMessage("test",null, new Locale("en")));

		// 构建工具类,获取所有的bean对象
		System.out.println(ApplicationContextUtil.getBeanByName("userService"));

		// 加载资源: spring读取配置文件和包扫描都是用加载资源的方式
		Resource resource = applicationContext.getResource("file://E:\\IdeaProjects\\group9-wanjunjie\\java-architect\\spring-framework-5.3.10-main\\tuling\\src\\main\\java\\com\\zhouyu\\AppConfig.java");
		System.out.println(resource.getFilename());

//		UserService userService1 = new UserService();
//
//		for (Field field : userService1.getClass().getDeclaredFields()) {
//			if (field.isAnnotationPresent(Autowired.class)) {
//				field.set(userService1, ??);
//			}
//		}
//
//
//		for (Method method : userService1.getClass().getDeclaredMethods()) {
//			if (method.isAnnotationPresent(PostConstruct.class)) {
//				method.invoke(userService1, null);
//			}
//		}
//
//		if (userService1 instanceof InitializingBean) {
//			try {
//				((InitializingBean)userService1).afterPropertiesSet();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		ProxyFactory proxyFactory = new ProxyFactory();
//		proxyFactory.setTarget(userService1);
//		proxyFactory.addAdvice(new MethodInterceptor() {
//			@Nullable
//			@Override
//			public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
//				System.out.println("切面逻辑 before...");
//				Object result = invocation.proceed();
////				Object result = invocation.getMethod().invoke(invocation.getThis(), invocation.getArguments());
//				System.out.println("切面逻辑 after...");
//				return result;
//			}
//		});
//		UserService userService2  = (UserService) proxyFactory.getProxy();
//		userService2.test();

	}
}







