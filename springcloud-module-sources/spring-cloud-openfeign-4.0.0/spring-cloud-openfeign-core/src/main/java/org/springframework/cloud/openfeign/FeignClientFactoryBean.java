/*
 * Copyright 2013-2022 the original author or authors.
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

package org.springframework.cloud.openfeign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import feign.Capability;
import feign.Client;
import feign.Contract;
import feign.ExceptionPropagationPolicy;
import feign.Feign;
import feign.Logger;
import feign.QueryMapEncoder;
import feign.Request;
import feign.RequestInterceptor;
import feign.ResponseInterceptor;
import feign.Retryer;
import feign.Target.HardCodedTarget;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cloud.openfeign.clientconfig.FeignClientConfigurer;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.cloud.openfeign.loadbalancer.RetryableFeignBlockingLoadBalancerClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Spencer Gibb
 * @author Venil Noronha
 * @author Eko Kurniawan Khannedy
 * @author Gregor Zurowski
 * @author Matt King
 * @author Olga Maciaszek-Sharma
 * @author Ilia Ilinykh
 * @author Marcin Grzejszczak
 * @author Jonatan Ivanov
 * @author Sam Kruglov
 * @author Jasbir Singh
 * @author Hyeonmin Park
 * @author Felix Dittrich
 * @author Dominique Villard
 */
public class FeignClientFactoryBean
		implements FactoryBean<Object>, InitializingBean, ApplicationContextAware, BeanFactoryAware {

	/***********************************
	 * WARNING! Nothing in this class should be @Autowired. It causes NPEs because of some
	 * lifecycle race condition.
	 ***********************************/

	private static final Log LOG = LogFactory.getLog(FeignClientFactoryBean.class);

	private Class<?> type;

	private String name;

	private String url;

	private String contextId;

	private String path;

	private boolean dismiss404;

	private boolean inheritParentContext = true;

	private ApplicationContext applicationContext;

	private BeanFactory beanFactory;

	private Class<?> fallback = void.class;

	private Class<?> fallbackFactory = void.class;

	private int readTimeoutMillis = new Request.Options().readTimeoutMillis();

	private int connectTimeoutMillis = new Request.Options().connectTimeoutMillis();

	private boolean followRedirects = new Request.Options().isFollowRedirects();

	private boolean refreshableClient = false;

	private final List<FeignBuilderCustomizer> additionalCustomizers = new ArrayList<>();

	private String[] qualifiers = new String[] {};

	// For AOT testing
	public FeignClientFactoryBean() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating a FeignClientFactoryBean.");
		}
	}

	@Override
	public void afterPropertiesSet() {
		Assert.hasText(contextId, "Context id must be set");
		Assert.hasText(name, "Name must be set");
	}

	protected Feign.Builder feign(FeignClientFactory context) {
		FeignLoggerFactory loggerFactory = get(context, FeignLoggerFactory.class);
		Logger logger = loggerFactory.create(type);

		// 从容器中获取Feign.Builder
		// @formatter:off
		Feign.Builder builder = get(context, Feign.Builder.class)
				// required values
				.logger(logger)
				.encoder(get(context, Encoder.class))
				.decoder(get(context, Decoder.class))
				.contract(get(context, Contract.class));
		// @formatter:on
		// 进行配置
		configureFeign(context, builder);

		return builder;
	}

	private void applyBuildCustomizers(FeignClientFactory context, Feign.Builder builder) {
		Map<String, FeignBuilderCustomizer> customizerMap = context.getInstances(contextId,
				FeignBuilderCustomizer.class);

		if (customizerMap != null) {
			customizerMap.values().stream().sorted(AnnotationAwareOrderComparator.INSTANCE)
					.forEach(feignBuilderCustomizer -> feignBuilderCustomizer.customize(builder));
		}
		additionalCustomizers.forEach(customizer -> customizer.customize(builder));
	}

	protected void configureFeign(FeignClientFactory context, Feign.Builder builder) {
		// 在配置文件中可以配置feignClient的属性
		// 对于feignClient的配置有两种方式: 在配置文件中配置封装成FeignClientProperties, 还有就是在创建各种配置Bean对象
		FeignClientProperties properties = beanFactory != null ? beanFactory.getBean(FeignClientProperties.class)
				: applicationContext.getBean(FeignClientProperties.class);

		FeignClientConfigurer feignClientConfigurer = getOptional(context, FeignClientConfigurer.class);
		setInheritParentContext(feignClientConfigurer.inheritParentConfiguration());
		// 使用FeignClientProperties配置文件来配置feignClient
		if (properties != null && inheritParentContext) {
			if (properties.isDefaultToProperties()) {
				configureUsingConfiguration(context, builder);
				configureUsingProperties(properties.getConfig().get(properties.getDefaultConfig()), builder);
				configureUsingProperties(properties.getConfig().get(contextId), builder);
			}
			else {
				configureUsingProperties(properties.getConfig().get(properties.getDefaultConfig()), builder);
				configureUsingProperties(properties.getConfig().get(contextId), builder);
				configureUsingConfiguration(context, builder);
			}
		}
		else {
			// 使用容器中的各种配置bean来配置feignClient
			configureUsingConfiguration(context, builder);
		}
	}

	protected void configureUsingConfiguration(FeignClientFactory context, Feign.Builder builder) {
		Logger.Level level = getInheritedAwareOptional(context, Logger.Level.class);
		if (level != null) {
			builder.logLevel(level);
		}
		Retryer retryer = getInheritedAwareOptional(context, Retryer.class);
		if (retryer != null) {
			builder.retryer(retryer);
		}
		ErrorDecoder errorDecoder = getInheritedAwareOptional(context, ErrorDecoder.class);
		if (errorDecoder != null) {
			builder.errorDecoder(errorDecoder);
		}
		else {
			FeignErrorDecoderFactory errorDecoderFactory = getOptional(context, FeignErrorDecoderFactory.class);
			if (errorDecoderFactory != null) {
				ErrorDecoder factoryErrorDecoder = errorDecoderFactory.create(type);
				builder.errorDecoder(factoryErrorDecoder);
			}
		}
		Request.Options options = getInheritedAwareOptional(context, Request.Options.class);
		if (options == null) {
			options = getOptionsByName(context, contextId);
		}

		if (options != null) {
			builder.options(options);
			readTimeoutMillis = options.readTimeoutMillis();
			connectTimeoutMillis = options.connectTimeoutMillis();
			followRedirects = options.isFollowRedirects();
		}

		// 从容器中获取RequestInterceptor类型的bean, 所以我们将RequestInterceptor注册为bean对象设置到容器中
		// 这种拦截器就是全局生效, 对所有的feignClient都生效
		Map<String, RequestInterceptor> requestInterceptors = getInheritedAwareInstances(context,
				RequestInterceptor.class);
		if (requestInterceptors != null) {
			List<RequestInterceptor> interceptors = new ArrayList<>(requestInterceptors.values());
			AnnotationAwareOrderComparator.sort(interceptors);
			builder.requestInterceptors(interceptors);
		}
		ResponseInterceptor responseInterceptor = getInheritedAwareOptional(context, ResponseInterceptor.class);
		if (responseInterceptor != null) {
			builder.responseInterceptor(responseInterceptor);
		}
		QueryMapEncoder queryMapEncoder = getInheritedAwareOptional(context, QueryMapEncoder.class);
		if (queryMapEncoder != null) {
			builder.queryMapEncoder(queryMapEncoder);
		}
		if (dismiss404) {
			builder.dismiss404();
		}
		ExceptionPropagationPolicy exceptionPropagationPolicy = getInheritedAwareOptional(context,
				ExceptionPropagationPolicy.class);
		if (exceptionPropagationPolicy != null) {
			builder.exceptionPropagationPolicy(exceptionPropagationPolicy);
		}

		Map<String, Capability> capabilities = getInheritedAwareInstances(context, Capability.class);
		if (capabilities != null) {
			capabilities.values().stream().sorted(AnnotationAwareOrderComparator.INSTANCE)
					.forEach(builder::addCapability);
		}
	}

	protected void configureUsingProperties(FeignClientProperties.FeignClientConfiguration config,
			Feign.Builder builder) {
		if (config == null) {
			return;
		}

		if (config.getLoggerLevel() != null) {
			builder.logLevel(config.getLoggerLevel());
		}

		if (!refreshableClient) {
			connectTimeoutMillis = config.getConnectTimeout() != null ? config.getConnectTimeout()
					: connectTimeoutMillis;
			readTimeoutMillis = config.getReadTimeout() != null ? config.getReadTimeout() : readTimeoutMillis;
			followRedirects = config.isFollowRedirects() != null ? config.isFollowRedirects() : followRedirects;

			builder.options(new Request.Options(connectTimeoutMillis, TimeUnit.MILLISECONDS, readTimeoutMillis,
					TimeUnit.MILLISECONDS, followRedirects));
		}

		if (config.getRetryer() != null) {
			Retryer retryer = getOrInstantiate(config.getRetryer());
			builder.retryer(retryer);
		}

		if (config.getErrorDecoder() != null) {
			ErrorDecoder errorDecoder = getOrInstantiate(config.getErrorDecoder());
			builder.errorDecoder(errorDecoder);
		}

		if (config.getRequestInterceptors() != null && !config.getRequestInterceptors().isEmpty()) {
			// this will add request interceptor to builder, not replace existing
			for (Class<RequestInterceptor> bean : config.getRequestInterceptors()) {
				RequestInterceptor interceptor = getOrInstantiate(bean);
				builder.requestInterceptor(interceptor);
			}
		}

		if (config.getResponseInterceptor() != null) {
			builder.responseInterceptor(getOrInstantiate(config.getResponseInterceptor()));
		}

		if (config.getDismiss404() != null) {
			if (config.getDismiss404()) {
				builder.dismiss404();
			}
		}

		if (Objects.nonNull(config.getEncoder())) {
			builder.encoder(getOrInstantiate(config.getEncoder()));
		}

		addDefaultRequestHeaders(config, builder);
		addDefaultQueryParams(config, builder);

		if (Objects.nonNull(config.getDecoder())) {
			builder.decoder(getOrInstantiate(config.getDecoder()));
		}

		if (Objects.nonNull(config.getContract())) {
			builder.contract(getOrInstantiate(config.getContract()));
		}

		if (Objects.nonNull(config.getExceptionPropagationPolicy())) {
			builder.exceptionPropagationPolicy(config.getExceptionPropagationPolicy());
		}

		if (config.getCapabilities() != null) {
			config.getCapabilities().stream().map(this::getOrInstantiate).forEach(builder::addCapability);
		}

		if (config.getQueryMapEncoder() != null) {
			builder.queryMapEncoder(getOrInstantiate(config.getQueryMapEncoder()));
		}
	}

	private void addDefaultQueryParams(FeignClientProperties.FeignClientConfiguration config, Feign.Builder builder) {
		Map<String, Collection<String>> defaultQueryParameters = config.getDefaultQueryParameters();
		if (Objects.nonNull(defaultQueryParameters)) {
			builder.requestInterceptor(requestTemplate -> {
				Map<String, Collection<String>> queries = requestTemplate.queries();
				defaultQueryParameters.keySet().forEach(key -> {
					if (!queries.containsKey(key)) {
						requestTemplate.query(key, defaultQueryParameters.get(key));
					}
				});
			});
		}
	}

	private void addDefaultRequestHeaders(FeignClientProperties.FeignClientConfiguration config,
			Feign.Builder builder) {
		Map<String, Collection<String>> defaultRequestHeaders = config.getDefaultRequestHeaders();
		if (Objects.nonNull(defaultRequestHeaders)) {
			builder.requestInterceptor(requestTemplate -> {
				Map<String, Collection<String>> headers = requestTemplate.headers();
				defaultRequestHeaders.keySet().forEach(key -> {
					if (!headers.containsKey(key)) {
						requestTemplate.header(key, defaultRequestHeaders.get(key));
					}
				});
			});
		}
	}

	private <T> T getOrInstantiate(Class<T> tClass) {
		try {
			return beanFactory != null ? beanFactory.getBean(tClass) : applicationContext.getBean(tClass);
		}
		catch (NoSuchBeanDefinitionException e) {
			return BeanUtils.instantiateClass(tClass);
		}
	}

	protected <T> T get(FeignClientFactory context, Class<T> type) {
		T instance = context.getInstance(contextId, type);
		if (instance == null) {
			throw new IllegalStateException("No bean found of type " + type + " for " + contextId);
		}
		return instance;
	}

	protected <T> T getOptional(FeignClientFactory context, Class<T> type) {
		return context.getInstance(contextId, type);
	}

	protected <T> T getInheritedAwareOptional(FeignClientFactory context, Class<T> type) {
		if (inheritParentContext) {
			return getOptional(context, type);
		}
		else {
			return context.getInstanceWithoutAncestors(contextId, type);
		}
	}

	protected <T> Map<String, T> getInheritedAwareInstances(FeignClientFactory context, Class<T> type) {
		if (inheritParentContext) {
			return context.getInstances(contextId, type);
		}
		else {
			return context.getInstancesWithoutAncestors(contextId, type);
		}
	}

	protected <T> T loadBalance(Feign.Builder builder, FeignClientFactory context, HardCodedTarget<T> target) {
		// 如果使用负载均衡, 返回的是FeignBlockingLoadBalancerClient
		Client client = getOptional(context, Client.class);
		if (client != null) {
			builder.client(client);
			applyBuildCustomizers(context, builder);
			Targeter targeter = get(context, Targeter.class);
			return targeter.target(this, builder, context, target);
		}

		throw new IllegalStateException(
				"No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-loadbalancer?");
	}

	/**
	 * Meant to get Options bean from context with bean name.
	 * @param context context of Feign client
	 * @param contextId name of feign client
	 * @return returns Options found in context
	 */
	protected Request.Options getOptionsByName(FeignClientFactory context, String contextId) {
		if (refreshableClient) {
			return context.getInstance(contextId, Request.Options.class.getCanonicalName() + "-" + contextId,
					Request.Options.class);
		}
		return null;
	}

	@Override
	public Object getObject() {
		return getTarget();
	}

	/**
	 * OpenFeign生成代理对象的逻辑
	 * @param <T> the target type of the Feign client
	 * @return a {@link Feign} client created with the specified data and the context
	 * information
	 */
	@SuppressWarnings("unchecked")
	<T> T getTarget() {
		// 重点: 从容器中获取FeignClientFactory, 创建feignClient的工厂
		// 类似与LoadBalancerClientFactory, 都继承了NamedContextFactory, 也会构建子容器
		// feignClient也是按照contextId不同的服务来进行容器区分,用于微服务调用场景
		FeignClientFactory feignClientFactory = beanFactory != null ? beanFactory.getBean(FeignClientFactory.class)
				: applicationContext.getBean(FeignClientFactory.class);

		// 使用建造者的设计模式创建feignClient对象
		// 在builder中设置了feignClient的encoder, decoder, logger等各种属性
		Feign.Builder builder = feign(feignClientFactory);

		// 如果feignClient没有配置url属性, 可以走负载均衡
		if (!StringUtils.hasText(url) && !isUrlAvailableInConfig(contextId)) {

			if (LOG.isInfoEnabled()) {
				LOG.info("For '" + name + "' URL not provided. Will try picking an instance via load-balancing.");
			}
			if (!name.startsWith("http")) {
				url = "http://" + name;
			}
			else {
				url = name;
			}
			url += cleanPath();
			// 调用#loadBalance生成负载均衡的代理对象
			return (T) loadBalance(builder, feignClientFactory, new HardCodedTarget<>(type, name, url));
		}

		// 直接用url来配置使用的feignClient
		if (StringUtils.hasText(url) && !url.startsWith("http")) {
			url = "http://" + url;
		}
		String url = this.url + cleanPath();
		// 从容器中获取client对象, 有OkHttpClient, ApacheHttp5Client等
		Client client = getOptional(feignClientFactory, Client.class);
		if (client != null) {
			// OpenFeign代理对象的最终是FeignBlockingLoadBalancerClient类型
			if (client instanceof FeignBlockingLoadBalancerClient) {
				// not load balancing because we have a url,
				// but Spring Cloud LoadBalancer is on the classpath, so unwrap
				client = ((FeignBlockingLoadBalancerClient) client).getDelegate();
			}
			if (client instanceof RetryableFeignBlockingLoadBalancerClient) {
				// not load balancing because we have a url,
				// but Spring Cloud LoadBalancer is on the classpath, so unwrap
				client = ((RetryableFeignBlockingLoadBalancerClient) client).getDelegate();
			}
			// 将client设置到Feign.Builder属性中
			builder.client(client);
		}
		// 提供扩展: Feign.Builder的自定义器
		applyBuildCustomizers(feignClientFactory, builder);

		// 从容器中获取Targeter, 默认使用的DefaultTargeter, 如果引入了熔断器使用则是FeignCircuitBreakerTargeter
		Targeter targeter = get(feignClientFactory, Targeter.class);
		// 生成feignClient的代理对象
		return targeter.target(this, builder, feignClientFactory, resolveTarget(feignClientFactory, contextId, url));
	}

	private String cleanPath() {
		if (path == null) {
			return "";
		}
		String path = this.path.trim();
		if (StringUtils.hasLength(path)) {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
		}
		return path;
	}

	/**
	 * 解析feignClient的代理对象类型
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> HardCodedTarget<T> resolveTarget(FeignClientFactory context, String contextId, String url) {
		if (StringUtils.hasText(url)) {
			return new HardCodedTarget(type, name, url);
		}

		if (refreshableClient) {
			RefreshableUrl refreshableUrl = context.getInstance(contextId,
					RefreshableUrl.class.getCanonicalName() + "-" + contextId, RefreshableUrl.class);
			if (Objects.nonNull(refreshableUrl) && StringUtils.hasText(refreshableUrl.getUrl())) {
				return new RefreshableHardCodedTarget<>(type, name, refreshableUrl);
			}
		}
		FeignClientProperties.FeignClientConfiguration config = findConfigByKey(contextId);
		if (Objects.isNull(config) || !StringUtils.hasText(config.getUrl())) {
			throw new IllegalStateException(
					"Provide Feign client URL either in @FeignClient() or in config properties.");
		}

		return new HardCodedTarget(type, name, FeignClientsRegistrar.getUrl(config.getUrl()));
	}

	private boolean isUrlAvailableInConfig(String contextId) {
		FeignClientProperties.FeignClientConfiguration config = findConfigByKey(contextId);
		return Objects.nonNull(config) && StringUtils.hasText(config.getUrl());
	}

	private FeignClientProperties.FeignClientConfiguration findConfigByKey(String configKey) {
		FeignClientProperties properties = beanFactory != null ? beanFactory.getBean(FeignClientProperties.class)
				: applicationContext.getBean(FeignClientProperties.class);
		return properties.getConfig().get(configKey);
	}

	@Override
	public Class<?> getObjectType() {
		return type;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isDismiss404() {
		return dismiss404;
	}

	public void setDismiss404(boolean dismiss404) {
		this.dismiss404 = dismiss404;
	}

	public boolean isInheritParentContext() {
		return inheritParentContext;
	}

	public void setInheritParentContext(boolean inheritParentContext) {
		this.inheritParentContext = inheritParentContext;
	}

	public void addCustomizer(FeignBuilderCustomizer customizer) {
		additionalCustomizers.add(customizer);
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
		beanFactory = context;
	}

	public Class<?> getFallback() {
		return fallback;
	}

	public void setFallback(Class<?> fallback) {
		this.fallback = fallback;
	}

	public Class<?> getFallbackFactory() {
		return fallbackFactory;
	}

	public void setFallbackFactory(Class<?> fallbackFactory) {
		this.fallbackFactory = fallbackFactory;
	}

	public void setRefreshableClient(boolean refreshableClient) {
		this.refreshableClient = refreshableClient;
	}

	public String[] getQualifiers() {
		return qualifiers;
	}

	public void setQualifiers(String[] qualifiers) {
		this.qualifiers = qualifiers;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FeignClientFactoryBean that = (FeignClientFactoryBean) o;
		return Objects.equals(applicationContext, that.applicationContext)
				&& Objects.equals(beanFactory, that.beanFactory) && dismiss404 == that.dismiss404
				&& inheritParentContext == that.inheritParentContext && Objects.equals(fallback, that.fallback)
				&& Objects.equals(fallbackFactory, that.fallbackFactory) && Objects.equals(name, that.name)
				&& Objects.equals(path, that.path) && Objects.equals(type, that.type) && Objects.equals(url, that.url)
				&& Objects.equals(connectTimeoutMillis, that.connectTimeoutMillis)
				&& Objects.equals(readTimeoutMillis, that.readTimeoutMillis)
				&& Objects.equals(followRedirects, that.followRedirects)
				&& Objects.equals(refreshableClient, that.refreshableClient);
	}

	@Override
	public int hashCode() {
		return Objects.hash(applicationContext, beanFactory, dismiss404, inheritParentContext, fallback,
				fallbackFactory, name, path, type, url, readTimeoutMillis, connectTimeoutMillis, followRedirects,
				refreshableClient);
	}

	@Override
	public String toString() {
		return new StringBuilder("FeignClientFactoryBean{").append("type=").append(type).append(", ").append("name='")
				.append(name).append("', ").append("url='").append(url).append("', ").append("path='").append(path)
				.append("', ").append("dismiss404=").append(dismiss404).append(", ").append("inheritParentContext=")
				.append(inheritParentContext).append(", ").append("applicationContext=").append(applicationContext)
				.append(", ").append("beanFactory=").append(beanFactory).append(", ").append("fallback=")
				.append(fallback).append(", ").append("fallbackFactory=").append(fallbackFactory).append("}")
				.append("connectTimeoutMillis=").append(connectTimeoutMillis).append("}").append("readTimeoutMillis=")
				.append(readTimeoutMillis).append("}").append("followRedirects=").append(followRedirects)
				.append("refreshableClient=").append(refreshableClient).append("}").toString();
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
