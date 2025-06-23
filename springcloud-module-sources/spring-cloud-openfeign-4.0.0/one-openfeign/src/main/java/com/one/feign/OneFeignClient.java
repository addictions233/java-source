package com.one.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description: openfeign的客户端
 * @author: wanjunjie
 * @date: 2024/09/25
 */
@FeignClient(name = "oneFeignClient", url = "http://127.0.0.1:8080",configuration = OneFeignClientConfiguration.class)
public interface OneFeignClient {

	@GetMapping("/hello")
	String helloFeign(@RequestParam("name") String name);
}
