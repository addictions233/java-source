package com.one.customLoadBalancer;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;

/**
 * @author one
 */
public class CustomLoadBalancerConfiguration {

	/**
	 * 注入自定义的负载均衡策略, 替换LoadBalancer默认的负责均衡策略
	 */
    @Bean
    public ReactorServiceInstanceLoadBalancer customLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        return new CustomRandomLoadBalancer(serviceInstanceListSupplierProvider);
    }
}
