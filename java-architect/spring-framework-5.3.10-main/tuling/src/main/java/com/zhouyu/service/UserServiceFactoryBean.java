package com.zhouyu.service;

import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author one
 * @description 使用factoryBean会创建两个bean对象,区别于使用@Bean注解
 * @date 2023-1-23
 */
@Component
public class UserServiceFactoryBean implements SmartFactoryBean<UserService> {

	@Override
	public UserService getObject() throws Exception {
		return new UserService(null);
//		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return UserService.class;
	}

	@Override
	public boolean isEagerInit() {
		return true;
	}
}
