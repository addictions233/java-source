package com.one.customLoadBalancer;

import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import reactor.core.publisher.Mono;

/**
 * @author one
 * 自定义负载均衡策略: 随机, LoadBalancer自定义负载均衡策略需要实现ReactorServiceInstanceLoadBalancer接口
 */
public class CustomRandomLoadBalancer implements ReactorServiceInstanceLoadBalancer {
	/**
	 * 服务列表
	 */
	private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public CustomRandomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable();
        return supplier.get().next().map(this::getInstanceResponse);
    }

    /**
     * 使用随机数获取服务
     * @param instances 实例列表
     * @return ServiceInstance
     */
    private Response<ServiceInstance> getInstanceResponse(
            List<ServiceInstance> instances) {
        System.out.println("CustomRandomLoadBalancerClient start");
        if (instances.isEmpty()) {
            return new EmptyResponse();
        }

        System.out.println("CustomRandomLoadBalancerClient random");
        // 随机算法
        int size = instances.size();
        Random random = new Random();
        ServiceInstance instance = instances.get(random.nextInt(size));

        return new DefaultResponse(instance);
    }
}
