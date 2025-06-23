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

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

/**
 * k1 LoadBalancerInterceptor实现了ClientHttpRequestInterceptor接口，因此也实现intercept方法，
 *   通过拦截器实现在发出http请求前实现负载均衡。
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Ryan Baxter
 * @author William Tran
 */
public class LoadBalancerInterceptor implements ClientHttpRequestInterceptor {

	/**
	 * 负载均衡器LoadBalancerClient只有一个实现类是BlockingLoadBalancerClient
	 */
	private LoadBalancerClient loadBalancer;

	private LoadBalancerRequestFactory requestFactory;

	public LoadBalancerInterceptor(LoadBalancerClient loadBalancer, LoadBalancerRequestFactory requestFactory) {
		this.loadBalancer = loadBalancer;
		this.requestFactory = requestFactory;
	}

	public LoadBalancerInterceptor(LoadBalancerClient loadBalancer) {
		// for backwards compatibility
		this(loadBalancer, new LoadBalancerRequestFactory(loadBalancer));
	}

	/**
	 * LoadBalancerInterceptor实现了ClientHttpRequestInterceptor接口，
	 * 因此重写了#intercept方法，用于实现负载均衡的请求拦截处理。
	 */
	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
			final ClientHttpRequestExecution execution) throws IOException {
		// 获取请求的URI
		final URI originalUri = request.getURI();
		// 获取service名称
		String serviceName = originalUri.getHost();
		Assert.state(serviceName != null, "Request URI does not contain a valid hostname: " + originalUri);
		// 使用LoadBalancerClient发起负载均衡请求调用
		return this.loadBalancer.execute(serviceName, this.requestFactory.createRequest(request, body, execution));
	}

}
