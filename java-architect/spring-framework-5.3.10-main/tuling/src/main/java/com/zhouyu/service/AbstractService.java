package com.zhouyu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

/**
 * @author one
 * @description IOC容器默认是不会扫描 接口和抽象类的, 但是有种特殊情况
 * 				如果该抽象类中有方法被@Lookup注解修饰了,就能扫描
 * @date 2023-2-25
 */
@Service
public abstract class AbstractService {
	@Autowired
	private OrderService orderService;

	/**
	 * Lookup注解方法根据名称或类型查找Bean对象, 并作为该方法的返回值
	 * @return OrderService
	 */
	@Lookup("orderService")
	public OrderService test() {
		System.out.println("this is abstractService test method");
		return null;
	}

	public void print() {
		// 调用test()方法返回一个OrderService的bean对象, Lookup返回的对象是多列的, 所以地址值不一样
		System.out.println("print1:" + test());;
	}

	public void print2() {
		// 因为AbstractService是单例的,所以注入的orderService虽然是protoType, 但是该对象是AbstractService初始化时就创建的, 所以还是同一个orderService对象
		System.out.println("print2:" + orderService);;
	}
}
