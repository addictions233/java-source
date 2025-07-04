/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.context.named;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aot.AotDetector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;

/**
 * Creates a set of child contexts that allows a set of Specifications to define the beans
 * in each child context.
 *
 * Ported from spring-cloud-netflix FeignClientFactory and SpringClientFactory
 *
 * @param <C> specification
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Tommy Karlsson
 * @author Olga Maciaszek-Sharma
 */
public abstract class NamedContextFactory<C extends NamedContextFactory.Specification>
		implements DisposableBean, ApplicationContextAware {

	private final Map<String, ApplicationContextInitializer<GenericApplicationContext>> applicationContextInitializers;

	private final String propertySourceName;

	private final String propertyName;

	/**
	 * key是ServiceId也就是服务名称, value是Spring 上下文容器, 每个服务一个IOC容器
	 */
	private final Map<String, GenericApplicationContext> contexts = new ConcurrentHashMap<>();

	private Map<String, C> configurations = new ConcurrentHashMap<>();

	private ApplicationContext parent;

	private Class<?> defaultConfigType;

	public NamedContextFactory(Class<?> defaultConfigType, String propertySourceName, String propertyName) {
		this(defaultConfigType, propertySourceName, propertyName, new HashMap<>());
	}

	public NamedContextFactory(Class<?> defaultConfigType, String propertySourceName, String propertyName,
			Map<String, ApplicationContextInitializer<GenericApplicationContext>> applicationContextInitializers) {
		this.defaultConfigType = defaultConfigType;
		this.propertySourceName = propertySourceName;
		this.propertyName = propertyName;
		this.applicationContextInitializers = applicationContextInitializers;
	}

	/**
	 * 将spring ioc容器设置其父容器
	 */
	@Override
	public void setApplicationContext(ApplicationContext parent) throws BeansException {
		this.parent = parent;
	}

	public ApplicationContext getParent() {
		return parent;
	}

	public void setConfigurations(List<C> configurations) {
		for (C client : configurations) {
			this.configurations.put(client.getName(), client);
		}
	}

	public Set<String> getContextNames() {
		return new HashSet<>(this.contexts.keySet());
	}

	@Override
	public void destroy() {
		Collection<GenericApplicationContext> values = this.contexts.values();
		for (GenericApplicationContext context : values) {
			// This can fail, but it never throws an exception (you see stack traces
			// logged as WARN).
			context.close();
		}
		this.contexts.clear();
	}

	protected GenericApplicationContext getContext(String name) {
		if (!this.contexts.containsKey(name)) {
			synchronized (this.contexts) {
				if (!this.contexts.containsKey(name)) {
					this.contexts.put(name, createContext(name));
				}
			}
		}
		return this.contexts.get(name);
	}

	/**
	 * NamedContextFactory调用#creatConext创建子容器, 每个服务一个子容器
	 */
	public GenericApplicationContext createContext(String name) {
		GenericApplicationContext context = buildContext(name);
		// there's an AOT initializer for this context
		if (applicationContextInitializers.get(name) != null) {
			applicationContextInitializers.get(name).initialize(context);
			context.refresh();
			return context;
		}
		// 子容器注册Bean
		registerBeans(name, context);
		context.refresh();
		return context;
	}

	public void registerBeans(String name, GenericApplicationContext context) {
		Assert.isInstanceOf(AnnotationConfigRegistry.class, context);
		AnnotationConfigRegistry registry = (AnnotationConfigRegistry) context;

		// 加载属于当前serviceId的配置, 也就是每个服务容器特有的配置
		if (this.configurations.containsKey(name)) {
			for (Class<?> configuration : this.configurations.get(name).getConfiguration()) {
				registry.register(configuration);
			}
		}

		// 加载所有的“默认”配置（也就是以default.开头的配置项）, 也就是全局配置
		for (Map.Entry<String, C> entry : this.configurations.entrySet()) {
			if (entry.getKey().startsWith("default.")) {
				for (Class<?> configuration : entry.getValue().getConfiguration()) {
					registry.register(configuration);
				}
			}
		}

		// 加载配置文件（从配置文件及环境变量中加载），注册配置类 this.defaultConfigType，
		// defaultConfigType其实就是LoadBalancerClientConfiguration配置类
		registry.register(PropertyPlaceholderAutoConfiguration.class, this.defaultConfigType);
	}

	public GenericApplicationContext buildContext(String name) {
		GenericApplicationContext context;

		// 父容器就是Spring ioc容器
		if (this.parent != null) {
			// jdk11 issue
			// https://github.com/spring-cloud/spring-cloud-netflix/issues/3101
			// https://github.com/spring-cloud/spring-cloud-openfeign/issues/475
			DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
			if (parent instanceof ConfigurableApplicationContext) {
				beanFactory.setBeanClassLoader(
						((ConfigurableApplicationContext) parent).getBeanFactory().getBeanClassLoader());
			}
			else {
				beanFactory.setBeanClassLoader(parent.getClassLoader());
			}

			// 构建一个新的容器作为子容器, 通常就是AnnotationConfigApplicationContext
			context = AotDetector.useGeneratedArtifacts() ? new GenericApplicationContext(beanFactory)
					: new AnnotationConfigApplicationContext(beanFactory);
			context.setClassLoader(parent.getClassLoader());
		}
		else {
			context = AotDetector.useGeneratedArtifacts() ? new GenericApplicationContext()
					: new AnnotationConfigApplicationContext();
		}
		// 设置子容器的环境变量
		context.getEnvironment().getPropertySources().addFirst(
				new MapPropertySource(this.propertySourceName, Collections.singletonMap(this.propertyName, name)));
		if (this.parent != null) {
			// Uses Environment from parent as well as beans
			// 设置子容器的父容器
			context.setParent(this.parent);
		}
		// 设置子容器的名称
		context.setDisplayName(generateDisplayName(name));
		return context;
	}

	protected String generateDisplayName(String name) {
		return this.getClass().getSimpleName() + "-" + name;
	}

	public <T> T getInstance(String name, Class<T> type) {
		// 根据name获取IOC容器, 也就是获取服务名称对应的上下文容器
		GenericApplicationContext context = getContext(name);
		try {
			// 在IOC容器中获取type类型的bean对象
			return context.getBean(type);
		}
		catch (NoSuchBeanDefinitionException e) {
			// ignore
		}
		return null;
	}

	public <T> ObjectProvider<T> getLazyProvider(String name, Class<T> type) {
		return new ClientFactoryObjectProvider<>(this, name, type);
	}

	public <T> ObjectProvider<T> getProvider(String name, Class<T> type) {
		GenericApplicationContext context = getContext(name);
		return context.getBeanProvider(type);
	}

	public <T> T getInstance(String name, Class<?> clazz, Class<?>... generics) {
		ResolvableType type = ResolvableType.forClassWithGenerics(clazz, generics);
		return getInstance(name, type);
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, ResolvableType type) {
		GenericApplicationContext context = getContext(name);
		String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, type);
		if (beanNames.length > 0) {
			for (String beanName : beanNames) {
				if (context.isTypeMatch(beanName, type)) {
					return (T) context.getBean(beanName);
				}
			}
		}
		return null;
	}

	public <T> Map<String, T> getInstances(String name, Class<T> type) {
		GenericApplicationContext context = getContext(name);

		return BeanFactoryUtils.beansOfTypeIncludingAncestors(context, type);
	}

	public Map<String, C> getConfigurations() {
		return configurations;
	}

	/**
	 * Specification with name and configuration.
	 */
	public interface Specification {

		String getName();

		Class<?>[] getConfiguration();

	}

}
