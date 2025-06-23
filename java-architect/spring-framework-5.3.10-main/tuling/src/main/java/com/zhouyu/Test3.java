package com.zhouyu;

import com.zhouyu.service.OrderService;
import com.zhouyu.service.ProductService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author one
 * @description 学习Spring依赖注入: @Autowired, @value, @Qualifier
 * @date 2023-11-1
 */
public class Test3 {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

		OrderService orderService = (OrderService) applicationContext.getBean("orderService");
		orderService.test();


		ProductService productService = (ProductService) applicationContext.getBean("productService");
		productService.test();
	}
}
