package com.zhouyu;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author one
 * @description 学习Bean生命周期函数销毁
 * @date 2023-9-1
 */
public class Main3 {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

		applicationContext.registerShutdownHook();
	}
}
