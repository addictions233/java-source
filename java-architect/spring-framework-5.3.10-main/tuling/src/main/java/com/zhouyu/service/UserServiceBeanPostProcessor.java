package com.zhouyu.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author one
 * @description 创建一个Bean对象就会调用一次BeanPostProcessor, 防止多次调用要加判断条件
 * @date 2023-2-22
 */
@Component
public class UserServiceBeanPostProcessor implements BeanPostProcessor {
	/**
	 * bean对象初始化前调用的方法
	 *
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof UserService) {
			System.out.println("userService Bean对象初始化前");
		}
		return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
	}

	/**
	 * bean对象初始化后调用的方法
	 *
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof  UserService) {
			System.out.println("userService Bean对象初初始化后");
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}
