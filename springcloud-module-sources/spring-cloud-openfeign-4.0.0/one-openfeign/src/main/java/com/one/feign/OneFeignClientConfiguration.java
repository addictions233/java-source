package com.one.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @description: openfeign的配置类
 * @author: wanjunjie
 * @date: 2024/09/25
 */
public class OneFeignClientConfiguration implements RequestInterceptor {
	@Override
	public void apply(RequestTemplate template) {
		System.out.println("自定义拦截器执行了...");
	}
}
