package com.zhouyu.mybatis;

import com.zhouyu.AppConfig;
import com.zhouyu.mybatis.mapper.UserMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author one
 * @description 测试spring整合mybatis
 * @date 2024-3-3
 */
public class Main {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		UserMapper userMapper = context.getBean(UserMapper.class);
		System.out.println(userMapper.selectById());
	}
}
