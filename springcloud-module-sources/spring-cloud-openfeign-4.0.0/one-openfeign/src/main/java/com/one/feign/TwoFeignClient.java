package com.one.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description: TODO
 * @author: wanjunjie
 * @date: 2024/09/26
 */
@FeignClient(name = "twoFeignClient", url = "http://127.0.0.1:8080")
public interface TwoFeignClient {

	@GetMapping("/hello")
	String helloFeign(@RequestParam("name") String name);
}
