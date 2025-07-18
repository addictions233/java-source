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

package org.springframework.cloud.loadbalancer.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

/**
 * A factory that creates client, load balancer and client configuration instances. It
 * creates a Spring ApplicationContext per client name, and extracts the beans that it
 * needs from there.
 *
 * LoadBalancerClientFactory客户端实现了不同的负载均衡算法，比如轮询、随机等。
 * LoadBalancerClientFactory继承自NamedContextFactory，NamedContextFactory继承ApplicationContextAware，
 * 实现Spring ApplicationContext容器操作。
 *
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Olga Maciaszek-Sharma
 */
public class LoadBalancerClientFactory extends NamedContextFactory<LoadBalancerClientSpecification>
		implements ReactiveLoadBalancer.Factory<ServiceInstance> {

	private static final Log log = LogFactory.getLog(LoadBalancerClientFactory.class);

	/**
	 * Property source name for load balancer.
	 */
	public static final String NAMESPACE = "loadbalancer";

	/**
	 * Property for client name within the load balancer namespace.
	 */
	public static final String PROPERTY_NAME = NAMESPACE + ".client.name";

	private final LoadBalancerClientsProperties properties;

	public LoadBalancerClientFactory(LoadBalancerClientsProperties properties) {
		super(LoadBalancerClientConfiguration.class, NAMESPACE, PROPERTY_NAME, new HashMap<>());
		this.properties = properties;
	}

	public LoadBalancerClientFactory(LoadBalancerClientsProperties properties,
			Map<String, ApplicationContextInitializer<GenericApplicationContext>> applicationContextInitializers) {
		super(LoadBalancerClientConfiguration.class, NAMESPACE, PROPERTY_NAME, applicationContextInitializers);
		this.properties = properties;
	}

	public static String getName(Environment environment) {
		return environment.getProperty(PROPERTY_NAME);
	}

	/**
	 * BlockingLoadBalancerClient中持有LoadBalancerClientFactory通过调用其getInstance方法获取具体的负载均衡客户端。
	 * 通过工厂类LoadBalancerClientFactory获取具体的负载均衡器实例，
	 * 后面的loadBalancer.choose(request)调用其接口choose()方法实现根据负载均衡算法选择下一个服务器完成负载均衡，
	 * 而ReactiveLoadBalancer#getInstance(String serviceId) 有默认实现LoadBalancerClientFactory
	 */
	@Override
	public ReactiveLoadBalancer<ServiceInstance> getInstance(String serviceId) {
		// 获取服务ID serviceId对应的负载均衡策略
		return getInstance(serviceId, ReactorServiceInstanceLoadBalancer.class);
	}

	@Override
	public LoadBalancerProperties getProperties(String serviceId) {
		if (properties == null) {
			if (log.isWarnEnabled()) {
				log.warn("LoadBalancerClientsProperties is null. Please use the new constructor.");
			}
			return null;
		}
		if (serviceId == null || !properties.getClients().containsKey(serviceId)) {
			// no specific client properties, return default
			return properties;
		}
		// because specifics are overlayed on top of defaults, everything in `properties`,
		// unless overridden, is in `clientsProperties`
		return properties.getClients().get(serviceId);
	}

	@SuppressWarnings("unchecked")
	public LoadBalancerClientFactory withApplicationContextInitializers(
			Map<String, Object> applicationContextInitializers) {
		Map<String, ApplicationContextInitializer<GenericApplicationContext>> convertedInitializers = new HashMap<>();
		applicationContextInitializers.keySet()
				.forEach(contextId -> convertedInitializers.put(contextId,
						(ApplicationContextInitializer<GenericApplicationContext>) applicationContextInitializers
								.get(contextId)));
		return new LoadBalancerClientFactory(properties, convertedInitializers);
	}

}
