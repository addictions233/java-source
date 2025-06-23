package com.zhouyu.mybatis.spring;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Set;

/**
 * @author one
 * @description TODO
 * @date 2024-3-4
 */
public class MapperBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

	public MapperBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
		for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
			GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition();
			beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDefinition.getBeanClassName());
			beanDefinition.setBeanClassName(MapperFactoryBean.class.getName());
			beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
		}
		return beanDefinitionHolders;
	}



	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface();
	}
}
