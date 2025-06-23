package com.zhouyu.service.beanFactory;

import com.zhouyu.service.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author one
 * @description TODO
 * @date 2023-12-15
 */
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// 注册单例bean
		beanFactory.registerSingleton("user1231", new User("user1231","123123"));

		// 获取beanDefinition
		BeanDefinition beanDefinition = beanFactory.getBeanDefinition("orderService");
		beanDefinition.setAutowireCandidate(false);
	}
}
