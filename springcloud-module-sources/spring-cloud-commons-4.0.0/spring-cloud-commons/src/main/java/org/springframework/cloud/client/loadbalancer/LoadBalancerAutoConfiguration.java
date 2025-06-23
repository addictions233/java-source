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

package org.springframework.cloud.client.loadbalancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * Auto-configuration for blocking client-side load balancing.
 * K1 客户端的负载均衡器的自动配置类
 *
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Will Tran
 * @author Gang Li
 * @author Olga Maciaszek-Sharma
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RestTemplate.class)
@ConditionalOnBean(LoadBalancerClient.class)
@EnableConfigurationProperties(LoadBalancerClientsProperties.class)
public class LoadBalancerAutoConfiguration {

	/**
	 * 由于@LoadBalanced注解上有@Qualifier注解标注
	 * 所以这里会注入所有的有@Qualifier注解标注的的RestTemplate的bean对象
	 */
	@LoadBalanced
	@Autowired(required = false)
	private List<RestTemplate> restTemplates = Collections.emptyList();

	@Autowired(required = false)
	private List<LoadBalancerRequestTransformer> transformers = Collections.emptyList();

	/**
	 * 用于RestTemplate初始化的SmartInitializingSingleton对象, 在所有的Bean对象都初始完了之后执行
	 */
	@Bean
	public SmartInitializingSingleton loadBalancedRestTemplateInitializerDeprecated(
			final ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers) {
		return () -> restTemplateCustomizers.ifAvailable(customizers -> {
			// 将每一个拦截器注入到每一个RestTemplate的bean对象中
			for (RestTemplate restTemplate : LoadBalancerAutoConfiguration.this.restTemplates) {
				for (RestTemplateCustomizer customizer : customizers) {
					customizer.customize(restTemplate);
				}
			}
		});
	}

	@Bean
	@ConditionalOnMissingBean
	public LoadBalancerRequestFactory loadBalancerRequestFactory(LoadBalancerClient loadBalancerClient) {
		return new LoadBalancerRequestFactory(loadBalancerClient, this.transformers);
	}

	@Configuration(proxyBeanMethods = false)
	@Conditional(RetryMissingOrDisabledCondition.class)
	static class LoadBalancerInterceptorConfig {

		/**
		 * 创建LoadBalancerInterceptor拦截器的Bean对象, 最后会注入到RestTemplate的拦截器链中
		 */
		@Bean
		public LoadBalancerInterceptor loadBalancerInterceptor(LoadBalancerClient loadBalancerClient,
				LoadBalancerRequestFactory requestFactory) {
			return new LoadBalancerInterceptor(loadBalancerClient, requestFactory);
		}

		/**
		 * 初始化RestTemplateCustomizer的Bean对象,最后会调用RestTemplateCustomizer#customize自定义restTemplate
		 */
		@Bean
		@ConditionalOnMissingBean
		public RestTemplateCustomizer restTemplateCustomizer(final LoadBalancerInterceptor loadBalancerInterceptor) {

			// 就是向RestTemplate中添加loadBalancerInterceptor的拦截器
			return restTemplate -> {
				List<ClientHttpRequestInterceptor> list = new ArrayList<>(restTemplate.getInterceptors());
				// 将loadBalancerInterceptor添加到拦截器链
				list.add(loadBalancerInterceptor);
				// 设置restTemplate的请求拦截器
				restTemplate.setInterceptors(list);
			};
		}

	}

	private static class RetryMissingOrDisabledCondition extends AnyNestedCondition {

		RetryMissingOrDisabledCondition() {
			super(ConfigurationPhase.REGISTER_BEAN);
		}

		@ConditionalOnMissingClass("org.springframework.retry.support.RetryTemplate")
		static class RetryTemplateMissing {

		}

		@ConditionalOnProperty(value = "spring.cloud.loadbalancer.retry.enabled", havingValue = "false")
		static class RetryDisabled {

		}

	}

	/**
	 * Auto configuration for retry mechanism.
	 */
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(RetryTemplate.class)
	public static class RetryAutoConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public LoadBalancedRetryFactory loadBalancedRetryFactory() {
			return new LoadBalancedRetryFactory() {
			};
		}

	}

	/**
	 * Auto configuration for retry intercepting mechanism.
	 */
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(RetryTemplate.class)
	@ConditionalOnBean(ReactiveLoadBalancer.Factory.class)
	@ConditionalOnProperty(value = "spring.cloud.loadbalancer.retry.enabled", matchIfMissing = true)
	public static class RetryInterceptorAutoConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public RetryLoadBalancerInterceptor loadBalancerInterceptor(LoadBalancerClient loadBalancerClient,
				LoadBalancerRequestFactory requestFactory, LoadBalancedRetryFactory loadBalancedRetryFactory,
				ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory) {
			return new RetryLoadBalancerInterceptor(loadBalancerClient, requestFactory, loadBalancedRetryFactory,
					loadBalancerFactory);
		}

		@Bean
		@ConditionalOnMissingBean
		public RestTemplateCustomizer restTemplateCustomizer(
				final RetryLoadBalancerInterceptor loadBalancerInterceptor) {
			return restTemplate -> {
				List<ClientHttpRequestInterceptor> list = new ArrayList<>(restTemplate.getInterceptors());
				list.add(loadBalancerInterceptor);
				restTemplate.setInterceptors(list);
			};
		}

	}

}
