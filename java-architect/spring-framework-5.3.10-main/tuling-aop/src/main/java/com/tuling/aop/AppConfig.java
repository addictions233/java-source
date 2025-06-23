package com.tuling.aop;

import com.tuling.UserService;
import com.tuling.aop.advice.ZhouyuAroundAdvice;
import com.tuling.aop.advice.ZhouyuBeforeAdvice;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author one
 * @description spring实现aop功能的配置类
 * @date 2023-7-11
 */
@ComponentScan("com.tuling")
//@Import(DefaultAdvisorAutoProxyCreator.class) // 负责解析所有的defaultPointcutAdvisor 切面对象, 要让该切面生效必须有这个解析对象 (核心),但是不能解析aspectj注解
//@Import(AnnotationAwareAspectJAutoProxyCreator.class) // 负责解析 @Aspect 注解, 要使得aspectj的注解生效, 必须有该bean对象
@EnableAspectJAutoProxy(proxyTargetClass = true) // 使用@Enable*注解启用aspectj注解的支持, 本质上还是引入AnnotationAwareAspectJAutoProxyCreator类型的Bean对象
public class AppConfig {
	/**
	 * 第一种spring实现aop: 直接将ProxyFactoryBean代理对象交给IOC容器管理, 可以通过ProxyFactory#newProxy方法生成代理对象
	 * 我们自己定义的bean一般不会直接手动调用ProxyFactory#newProxy生成代理对象, 而是在初始化后的生命周期函数中利用BeanPostProcessor生成
	 * @return 代理bean对象
	 */
	@Bean
	public ProxyFactoryBean userService() {
		ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
		proxyFactoryBean.setTarget(new UserService());
		proxyFactoryBean.addAdvice(new ZhouyuBeforeAdvice());

		return proxyFactoryBean;
	}

	/**
	 * 第二种通过BeanPostProcessor的实例化后的生命周期函数代理bean对象: 本质上是一个BeanPostProcessor,
	 * 可以通过beanName进行匹配生成代理对象, 控制粒度比较大只能到BeanName级别, 我们通常需要判断对bean对象中具体哪个方法进行代理
	 *
	 * @return
	 */
	@Bean
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
		BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
		beanNameAutoProxyCreator.setBeanNames("userSer*"); // bean对象的名称满足 userSer* 表达式
		beanNameAutoProxyCreator.setInterceptorNames("zhouyuAroundAdvice"); // 通知的bean名称 (通知得交给IOC管理)
		return beanNameAutoProxyCreator;
	}

	/**
	 * advice通知 + pointcut切入点 = advisor切面
	 * 第三种以切面的方式实现aop,最接近spring的效果, 根据method决定是否进行代理
	 * 这种直接创建bean对象是没有用的,需要IOC容器中有DefaultAdvisorAutoProxyCreator这个bean对象,负责解析所有的DefaultPointcutAdvisor
	 * DefaultAdvisorAutoProxyCreator可以解析我们定义的BeforeAdvice, AroundAdvice等, 但是不能支持Aspectj注解
	 * @return
	 */
	@Bean
	public DefaultPointcutAdvisor defaultPointcutAdvisor() {
		// 定义切入点, 所有的test名称的joint point都是切入点
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.addMethodName("test");
		// 定义切面
		DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
		defaultPointcutAdvisor.setPointcut(pointcut); // 设置切入点
		defaultPointcutAdvisor.setAdvice(new ZhouyuAroundAdvice()); // 设置通知
		// 返回切面对象
		return defaultPointcutAdvisor;
	}

}
