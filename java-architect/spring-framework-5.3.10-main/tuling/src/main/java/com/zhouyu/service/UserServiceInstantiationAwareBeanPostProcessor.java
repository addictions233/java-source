package com.zhouyu.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

/**
 * @author one
 * @description InstantiationAwareBeanPostProcessor 继承了 BeanPostProcessor接口, 在创建bean对象之前调用
 * 		beanPostProcessor是在bean对象属性初始化前, 初始化后调用
 * 	    而InstantiationAwareBeanPostProcessor则提供了bean对象实例化前和实例化后调用的方法, 可以在该方法中自定义实例化bean对象的方法
 *		bean对象实例化: 创建bean对象
 *		bean对象初始化: 对bean对象属性进行赋值
 * @date 2023-5-16
 */
//@Component
public class UserServiceInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
	/**
	 * bean对象实例化前调用, 可以在该方法类自定义bean对象的实例化方法
	 *
	 * @param beanClass the class of the bean to be instantiated
	 * @param beanName the name of the bean
	 * @return bean对象
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		// 走自定义的创建bean对象的逻辑,不走spring原有的创建逻辑, 如果实例化前的生命周期函数返回了Bean对象
		// 那么这个Bean对象不走实例化及实例化后那一套生命周期, 后面只走初始化后的生命周期函数(进行AOP)
		if (beanName.equals("userService")) {
			try {
				System.out.println("使用InstantiationAwareBeanPostProcessor进行实例化");
				return beanClass.getConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * bean对象实例化之后调用
	 *
	 * @param bean the bean instance created, with properties not having been set yet
	 * @param beanName the name of the bean
	 * @return
	 * @throws BeansException
	 */
	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
	}

	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		return InstantiationAwareBeanPostProcessor.super.postProcessProperties(pvs, bean, beanName);
	}
}
