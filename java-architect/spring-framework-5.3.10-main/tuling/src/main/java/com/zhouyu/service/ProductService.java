package com.zhouyu.service;

import org.springframework.beans.factory.BeanNameAware;

/**
 * @author one
 * @description TODO
 * @date 2023-11-8
 */
//@Component
//@Primary
public class ProductService implements BeanNameAware {

	private OrderService orderService;

	private String beanName;

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public void test() {
//		System.out.println("productService中的test方法:" + orderService);
		System.out.println("productService中的beanName:" + beanName);
	}

	/**
	 * 可以获取到bean对象的名称
	 * @param beanName
	 */
	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
}
