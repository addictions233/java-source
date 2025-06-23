package com.zhouyu.service;

import com.zhouyu.UserCondition;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * spring创建bean对象的过程:
 * UserService.class
 * --> 无参构造方法: 有无参构造默认使用无参构造(推断构造方法), 如果有多个构造方法且没有无参构造会报错, spring也不知道使用哪个构造方法
 * --> 创建对象
 * --> 依赖注入(DI注入属性)
 * --> 初始化前(@PostConstruct)  会去调用被@PostConstruct修饰的方法
 * --> 初始化(initializingBean)    会去调用InitializingBean接口的afterPropertiesSet()方法
 * ---> 初始化后(AOP)  生成代理对象
 * --> bean对象
 */
@Service
@Conditional(UserCondition.class) // 满足condition条件才会创建对应的bean对象
//@DependsOn("orderService")
//@Lazy
//@Scope // 对于@Scope会缓存对于的构造方法,后续创建bean对象直接调用
public class UserService implements InitializingBean, DisposableBean {

	private OrderService orderService;

	@Autowired
	public void setOrderService(OrderService orderService) {
		System.out.println("userServiceBean 对象调用set方法进行DI注入");
		this.orderService = orderService;
	}

	/**
	 * 需要将admin属性在bean对象创建过程时就进行初始化
	 */
	@Order(2)
	private User admin;

	/**
	 * 使用字符串转换为对象, 利用spring提供的CustomEditorConfigurer功能
	 */
//	@Value("123")
	@Order(1)
	private User user;

	/**
	 * 可以获取spring中的事件发布器
	 */
	private ApplicationEventPublisher applicationEventPublisher;

	/**
	 * 创建bean对象必须提供默认的构造方法, 否则会报错
	 * @param orderService 创建bean对象的构造方法入参必须是bean对象, 否则spring无法拿到这个参数
	 */
//	@Autowired(required = false)
	public UserService(OrderService orderService) {
		this.orderService = orderService;
		System.out.println("使用一个参数构造方法创建userService bean对象");
	}

	/**
	 * 1, 如果所有的构造方法上都没有@Autowired注解, 有无参构造使用无参构造, 没有无参构造就只能有一个构造方法
	 * 2, 如果构造方法上加了@Autowired注解,且required属性为true,那么其他构造方法上不能加@Autowired注解,否则抛异常
	 * 3,
	 * @param orderService 参数1
	 * @param orderService1 参数2
	 */
	@Autowired(required = true)  // 指定IOC容器使用的构造方法
	public UserService(OrderService orderService,OrderService orderService1) {
		this.orderService = orderService;
		System.out.println("使用两个参数构造方法创建userService bean对象");
	}

	/**
	 * 无参构造:默认情况下使用无参构造方法
	 */
//	public UserService() {
//		System.out.println("无参构造方法 创建userService bean对象");
//	}

	@PostConstruct
	public void setAdmin() {
		this.admin = new User("admin","123");
		System.out.println("userService Bean对象的postConstruct方法调用了");
	}

	public void test(){
		System.out.println(orderService);
		System.out.println(this.admin);
		System.out.println(this.user);
	}

	public void testMergedBeanDefinition() {
		System.out.println("this method is testMergedBeanDefinition");
	}


	/**
	 * 在所有的属性赋值之后进行调用
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.admin =  new User("admin", "456");
		System.out.println("userService Bean对象的afterPropertiesSet方法调用了");
	}


	/**
	 * 设置bean销毁的调用方法
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		System.out.println("orderService bean对象被销毁");
	}

//	/**
//	 * 通过使用注解或者实现接口是相同效果
//	 */
//	@PreDestroy
//	public void annoDestroy() {
//		System.out.println("orderService bean对象被销毁");
//	}

}
