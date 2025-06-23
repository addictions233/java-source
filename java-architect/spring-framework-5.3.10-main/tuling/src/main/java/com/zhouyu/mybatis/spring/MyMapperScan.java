package com.zhouyu.mybatis.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author one
 * @description 定义扫描mapper接口路径的注解,同时引入MapperImportBeanDefinitionRegistry
 *              有些类似于@Enable*注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MapperImportBeanDefinitionRegistry.class)
public @interface MyMapperScan {
	/**
	 * 扫描mapper接口的路径
	 */
	String value();
}
