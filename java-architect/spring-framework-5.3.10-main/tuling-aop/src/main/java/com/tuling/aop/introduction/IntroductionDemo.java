package com.tuling.aop.introduction;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.annotation.Annotation;

/**
 * @author 周瑜
 */
//@ComponentScan
//@Configuration
//@EnableAspectJAutoProxy
public class IntroductionDemo {

	public static void main(String[] args) {
		// 测试Introduction技术, 让customService实现CustomInterface接口,该接口的实现方法在DefaultCustomInterface中实现
		AnnotationConfigApplicationContext applicationContext  = new AnnotationConfigApplicationContext(IntroductionDemo.class);
		CustomService customService = applicationContext.getBean(CustomService.class);
		CustomInterface customInterface = (CustomInterface) customService;
		customInterface.custom();

//		System.out.println(customService.getClass().isAnnotationPresent(CustomAnnotation.class));

		for (Annotation annotation : customService.getClass().getDeclaredAnnotations()) {
			System.out.println(annotation.getClass());
		}
	}
}
