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

package org.springframework.cloud.loadbalancer.blocking.client;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import reactor.core.publisher.Mono;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.CompletionContext;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.DefaultRequestContext;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.HttpRequestLoadBalancerRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycle;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycleValidator;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestAdapter;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.ResponseData;
import org.springframework.cloud.client.loadbalancer.TimedRequestContext;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.ReflectionUtils;

import static org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer.REQUEST;

/**
 * k1 对于LoadBalancerClient接口,Spring Cloud LoadBalancer提供了默认实现为BlockingLoadBalancerClient
 * The default {@link LoadBalancerClient} implementation.
 *
 * @author Olga Maciaszek-Sharma
 * @since 2.2.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BlockingLoadBalancerClient implements LoadBalancerClient {

	/**
	 * BlockingLoadBalancerClient中持有LoadBalancerClientFactory通过调用其#getInstance方法获取具体的负载均衡客户端
	 */
	private final ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory;

	public BlockingLoadBalancerClient(ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory) {
		this.loadBalancerClientFactory = loadBalancerClientFactory;
	}

	/**
	 * execute方法用于进行负载均衡逻辑，继承自ServiceInstanceChooser接口，从服务列表中选择出一个服务地址进行调用。
	 * 在LoadBalancerClient种存在两个execute()方法，均是用来执行请求的，reconstructURI()是用来重构URL。
	 */
	@Override
	public <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException {
		String hint = getHint(serviceId);

		// 使用适配器设计模式, 将DefaultRequest适配成LoadBalancerRequest
		LoadBalancerRequestAdapter<T, TimedRequestContext> lbRequest = new LoadBalancerRequestAdapter<>(request,
				buildRequestContext(request, hint));

		// 执行生命周期函数
		Set<LoadBalancerLifecycle> supportedLifecycleProcessors = getSupportedLifecycleProcessors(serviceId);
		supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStart(lbRequest));

		// 通过服务ID选出一个服务实例,来发送请求
		ServiceInstance serviceInstance = choose(serviceId, lbRequest);
		if (serviceInstance == null) {
			supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
					new CompletionContext<>(CompletionContext.Status.DISCARD, lbRequest, new EmptyResponse())));
			throw new IllegalStateException("No instances available for " + serviceId);
		}

		// 调用重载的#execute方法
		return execute(serviceId, serviceInstance, lbRequest);
	}

	private <T> TimedRequestContext buildRequestContext(LoadBalancerRequest<T> delegate, String hint) {
		if (delegate instanceof HttpRequestLoadBalancerRequest) {
			HttpRequest request = ((HttpRequestLoadBalancerRequest) delegate).getHttpRequest();
			if (request != null) {
				RequestData requestData = new RequestData(request);
				return new RequestDataContext(requestData, hint);
			}
		}
		return new DefaultRequestContext(delegate, hint);
	}

	@Override
	public <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request)
			throws IOException {
		DefaultResponse defaultResponse = new DefaultResponse(serviceInstance);
		Set<LoadBalancerLifecycle> supportedLifecycleProcessors = getSupportedLifecycleProcessors(serviceId);
		Request lbRequest = request instanceof Request ? (Request) request : new DefaultRequest<>();
		supportedLifecycleProcessors
				.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, new DefaultResponse(serviceInstance)));
		try {
			// 发送请求
			T response = request.apply(serviceInstance);

			// 获取响应
			Object clientResponse = getClientResponse(response);
			supportedLifecycleProcessors
					.forEach(lifecycle -> lifecycle.onComplete(new CompletionContext<>(CompletionContext.Status.SUCCESS,
							lbRequest, defaultResponse, clientResponse)));
			return response;
		}
		catch (IOException iOException) {
			supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
					new CompletionContext<>(CompletionContext.Status.FAILED, iOException, lbRequest, defaultResponse)));
			throw iOException;
		}
		catch (Exception exception) {
			supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
					new CompletionContext<>(CompletionContext.Status.FAILED, exception, lbRequest, defaultResponse)));
			ReflectionUtils.rethrowRuntimeException(exception);
		}
		return null;
	}

	private <T> Object getClientResponse(T response) {
		ClientHttpResponse clientHttpResponse = null;
		if (response instanceof ClientHttpResponse) {
			clientHttpResponse = (ClientHttpResponse) response;
		}
		if (clientHttpResponse != null) {
			try {
				return new ResponseData(clientHttpResponse, null);
			}
			catch (IOException ignored) {
			}
		}
		return response;
	}

	private Set<LoadBalancerLifecycle> getSupportedLifecycleProcessors(String serviceId) {
		return LoadBalancerLifecycleValidator.getSupportedLifecycleProcessors(
				loadBalancerClientFactory.getInstances(serviceId, LoadBalancerLifecycle.class),
				DefaultRequestContext.class, Object.class, ServiceInstance.class);
	}

	@Override
	public URI reconstructURI(ServiceInstance serviceInstance, URI original) {
		return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
	}

	@Override
	public ServiceInstance choose(String serviceId) {
		return choose(serviceId, REQUEST);
	}

	/**
	 * 通过不同的负载均衡客户端实现选择不同的服务
	 */
	@Override
	public <T> ServiceInstance choose(String serviceId, Request<T> request) {

		// 获取负载均衡器, ReactiveLoadBalancer接口的实现类, 默认为RoundRobinLoadBalancer轮询
		// loadBalancerClientFactory为每个服务构建一个上下文, 做到服务隔离
		ReactiveLoadBalancer<ServiceInstance> loadBalancer = loadBalancerClientFactory.getInstance(serviceId);
		if (loadBalancer == null) {
			return null;
		}

		// 通过负载均衡算法获取ServiceInstance服务实例
		Response<ServiceInstance> loadBalancerResponse = Mono.from(loadBalancer.choose(request)).block();
		if (loadBalancerResponse == null) {
			return null;
		}
		// 返回服务实例
		return loadBalancerResponse.getServer();
	}

	private String getHint(String serviceId) {
		LoadBalancerProperties properties = loadBalancerClientFactory.getProperties(serviceId);
		String defaultHint = properties.getHint().getOrDefault("default", "default");
		String hintPropertyValue = properties.getHint().get(serviceId);
		return hintPropertyValue != null ? hintPropertyValue : defaultHint;
	}

}
