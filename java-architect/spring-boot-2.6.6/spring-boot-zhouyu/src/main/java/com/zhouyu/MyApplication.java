package com.zhouyu;

import com.zhouyu.selector.MyDeferredImportSelector;
import com.zhouyu.service.UserEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * @author one
 */
@SpringBootApplication
@Import(MyDeferredImportSelector.class) // 测试DeferredImportSelector的执行逻辑, 延时解析
//@Import(MyImportSelector.class) // 对比ImportSelector和DeferredImportSelector的执行区别
//@Import(UserEntityConfiguration.class)
public class MyApplication {

	@Bean
	public UserEntity userEntity() {
		return new UserEntity("myUserEntity");
	}


	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getContextClassLoader());

		ConfigurableApplicationContext applicationContext = SpringApplication.run(MyApplication.class, args);
		Environment environment = applicationContext.getBean(Environment.class);
		Arrays.stream(environment.getActiveProfiles()).forEach(System.out::println);
		System.out.println(applicationContext);
	}

}
