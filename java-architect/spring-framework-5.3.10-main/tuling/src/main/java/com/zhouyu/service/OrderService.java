package com.zhouyu.service;

import com.zhouyu.service.qualifier.LoadBalance;
import com.zhouyu.service.qualifier.RoundRobin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
//@Scope("prototype")  // prototype表示每次使用该bean对象, 都会创建一个新的bean对象
//@DependsOn("userService")
public class OrderService{

	/**
	 * 同类型的bean找到多个,放入map中, key就是beanName, value是对应的bean对象
	 */
	@Autowired
	private Map<String, ProductService> productServiceMap;

	@Autowired
	@RoundRobin
	private LoadBalance loadBalance;

//	@Value("${zhouyu}")  // 使用${}占位符获取Environment对象中的键值对
	@Value("#{productService}") // 使用Spring el表达式获取beanName为zhouyu的bean对象
	private ProductService propertyValue;

	private User user;

	public void setUser(User user) {
		this.user = user;
	}



	public void test() {
//		System.out.println(user);
//		productService33.test();
//		System.out.println(productServiceMap);
		System.out.println(loadBalance);
	}





}
