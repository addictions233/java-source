package com.zhouyu.service;

import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author one
 * @description MergedBeanDefinitionPostProcessor 可以设置生命周期函数, 也可以设置属性填充值
 * @date 2023-6-2
 */
//@Component
public class UserServiceMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {

	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		if ("userService".equals(beanName)) {
			// 设置Bean对象的初始方法名称
			beanDefinition.setInitMethodName("testMegerdBeanDefinition");
			// 设置Bean对象销毁方法名称
			beanDefinition.setDestroyMethodName("destroy");
			// 设置属性注入的值
			OrderService orderService = new OrderService();
			beanDefinition.getPropertyValues().add("orderService", orderService);
		}
	}

	@Override
	public void resetBeanDefinition(String beanName) {
		MergedBeanDefinitionPostProcessor.super.resetBeanDefinition(beanName);
	}
}
