package com.zhouyu.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author one
 * @description 测试@Configuration(proxyBeanMethods = false) lite模式
 * @date 2024-3-17
 */
//@Configuration(proxyBeanMethods = false) // lite模式, @Bean方法不会被代理
@Configuration(proxyBeanMethods = true)  // 默认 full模式, @Bean方法会被代理
public class UserConfiguration {


	/**
	 * @Bean注解的方法, 默认是单例的, 也就是说IOC容器中只有一个对象
	 * 但是如果是lite的配置模式, 调用UserConfiguration#user方法, 每次都会创建一个新的对象
	 * 如果是full的配置模式, 调用UserConfiguration#user方法, 每次都是直接从IOC容器中获取对象,只在容器启动时调用一次#user
	 * 然后将生成的user对象放入IOC容器, 后续直接从容器中取
	 */
	@Bean
	public User user() {
		User user = new User();
		user.setUsername("zhangsan");
		user.setAge(18);
		user.setMyCar(car());
		return user;
	}

	@Bean
	public Car car() {
		return new Car("宝马", "红色");
	}
}
