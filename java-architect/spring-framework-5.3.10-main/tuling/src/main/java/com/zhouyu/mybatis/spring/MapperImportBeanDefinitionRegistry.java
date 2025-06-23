package com.zhouyu.mybatis.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.util.Map;

/**
 * @author one
 * @description 注册MapperFactoryBean
 * @date 2024-3-3
 */
public class MapperImportBeanDefinitionRegistry implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		// 获取扫描路径
		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(MyMapperScan.class.getName());
		String mapperPath = (String) annotationAttributes.get("value");

		// 扫描mapper接口
		MapperBeanDefinitionScanner scanner = new MapperBeanDefinitionScanner(registry);
		scanner.addIncludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)  {
				return true;
			}
		});
		scanner.scan(mapperPath);

	}
}
