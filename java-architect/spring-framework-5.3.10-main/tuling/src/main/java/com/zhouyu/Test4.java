package com.zhouyu;

import com.zhouyu.service.event.PriceReduceEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author one
 * @description 学习spring启动过程源码分析
 * @date 2023-12-14
 */
public class Test4 {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

//		AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
//		webApplicationContext.register(AppConfig.class);
//		webApplicationContext.refresh(); // webApplicationContext才支持多次刷新
//		webApplicationContext.refresh();
		context.publishEvent("2132131");
		context.publishEvent(new PriceReduceEvent("降价50块"));
		context.close(); // 关闭spring IOC容器, 会执行生命周期函数
	}
}
