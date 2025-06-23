package com.zhouyu.initializer;

import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * @author one
 * @description TODO
 * @date 2024-9-9
 */
public class MyApplicationContextInitializer implements ApplicationContextInitializer {

	/**
	 * 向bootstrapContext中注册扫描的类型排除过滤器
	 * @param applicationContext
	 */
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		applicationContext.getBeanFactory().registerSingleton("myTypeExcludeFilter",  new TypeExcludeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
				return metadataReader.getClassMetadata().getClassName().equals("com.zhouyu.service.UserService");
			}
		});
	}
}
