package com.tuling.aop;

import com.tuling.UserService;
import com.tuling.aop.advice.ZhouyuAfterReturningAdvice;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author 周瑜
 */
//@Configuration
public class DefaultAdvisorAutoProxyCreatorDemo {

	@Bean
	public UserService userService() {
		return new UserService();
	}

	/**
	 * 如果Bean对象中有test方法, 就会进行动态代理, 代理逻辑在ZhouyuAfterReturningAdvice中指定
	 * 生效的前提是IOC容器中有DefaultAdvisorAutoProxyCreator这个BeanPostProcessor
	 */
	@Bean
	public DefaultPointcutAdvisor defaultPointcutAdvisor() {
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.addMethodName("test");

		DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
		defaultPointcutAdvisor.setPointcut(pointcut);
		defaultPointcutAdvisor.setAdvice(new ZhouyuAfterReturningAdvice());

		return defaultPointcutAdvisor;
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);

		return defaultAdvisorAutoProxyCreator;
	}

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext =
				new AnnotationConfigApplicationContext(DefaultAdvisorAutoProxyCreatorDemo.class);
		UserService userService = applicationContext.getBean("userService", UserService.class);
		userService.test();
	}
}
