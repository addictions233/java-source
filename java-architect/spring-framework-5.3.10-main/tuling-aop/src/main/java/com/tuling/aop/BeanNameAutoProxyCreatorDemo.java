package com.tuling.aop;

import com.tuling.UserService;
import com.tuling.aop.advice.ZhouyuAroundAdvice;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author 周瑜
 */
//@Configuration
public class BeanNameAutoProxyCreatorDemo {

	@Bean
	public UserService userService() {
		return new UserService();
	}

	@Bean
	public ZhouyuAroundAdvice zhouyuAroundAdvise() {
		return new ZhouyuAroundAdvice();
	}

	/**
	 * 直接指定beanName匹配userSe*的bean对象生成代理对象,代理逻辑在zhouyuAroundAdvise中指定
	 * 缺点: 只能做到beanName级别判断是否进行代理, 无法具体到bean对象中的某个方法级别
	 */
	@Bean
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
		BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
		beanNameAutoProxyCreator.setBeanNames("userSe*");
		beanNameAutoProxyCreator.setInterceptorNames("zhouyuAroundAdvise");
		beanNameAutoProxyCreator.setProxyTargetClass(true);

		return beanNameAutoProxyCreator;
	}

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext =
				new AnnotationConfigApplicationContext(BeanNameAutoProxyCreatorDemo.class);

		UserService userService = applicationContext.getBean("userService", UserService.class);
		userService.test();
	}
}
