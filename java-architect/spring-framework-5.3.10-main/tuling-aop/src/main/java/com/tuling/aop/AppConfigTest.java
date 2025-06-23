package com.tuling.aop;

import com.tuling.UserInterface;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author 周瑜
 */
public class AppConfigTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
		UserInterface userService = (UserInterface) applicationContext.getBean("userService");
//		userService.test2("aa");
		userService.test();
	}
}
