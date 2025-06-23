package com.zhouyu.service;

import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author one
 * @description 测试依赖注入
 * @date 2023-11-1
 */
//@Component
public class OrderServiceMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {

	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		if ("orderService".equals(beanName)) {
			// MergedBeanDefinitionPostProcessor中对Bean对象的属性赋值会覆盖Bean对象原有的依赖注入值
			beanDefinition.getPropertyValues().add("user", new User("张三","123"));
		}

	}
}
