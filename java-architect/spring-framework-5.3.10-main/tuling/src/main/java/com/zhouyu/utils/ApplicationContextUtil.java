package com.zhouyu.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author one
 * @description 将IOC容器进行初始化
 * @date 2023-1-17
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextUtil.applicationContext = applicationContext;
	}

	public static Object getBeanByName (String beanName) {
		return applicationContext.getBean(beanName);
	}


}
