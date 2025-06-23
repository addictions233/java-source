package com.tuling.transactional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author one
 * @description TODO
 * @date 2024-4-15
 */
public class Test {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TransactionAppConfig.class);
//		UserService userService = applicationContext.getBean(UserService.class);
//		userService.insert();

		TransactionService transactionService = applicationContext.getBean(TransactionService.class);
		transactionService.test();
	}
}
