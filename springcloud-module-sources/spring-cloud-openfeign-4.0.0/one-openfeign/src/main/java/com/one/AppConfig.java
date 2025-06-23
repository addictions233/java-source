package com.one;

import com.one.feign.OneFeignClient;
import com.one.feign.TwoFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @description: 扫描feignClient
 * @author: wanjunjie
 * @date: 2024/09/25
 */

@SpringBootApplication
@EnableFeignClients(basePackages = "com.one.feign")
public class AppConfig {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(AppConfig.class, args);
//		OneFeignClient feignClient = applicationContext.getBean(OneFeignClient.class);
		TwoFeignClient feignClient = applicationContext.getBean(TwoFeignClient.class);
		feignClient.helloFeign("123");

	}
}
