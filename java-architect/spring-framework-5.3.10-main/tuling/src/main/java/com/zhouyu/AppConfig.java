package com.zhouyu;

import com.zhouyu.mybatis.spring.MyMapperScan;
import com.zhouyu.service.OrderService;
import com.zhouyu.service.ProductService;
import com.zhouyu.service.User;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.beans.PropertyEditor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ComponentScan(value = "com.zhouyu", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = User.class))
@EnableScheduling
//@PropertySource("classpath:spring.properties") // 用来指定读取的配置文件的
@EnableAspectJAutoProxy(proxyTargetClass = true)
@MyMapperScan("com.zhouyu.mybatis.mapper")
public class AppConfig {

//	@Bean(autowire = Autowire.BY_NAME)
	@Bean
//	@Primary
	public ProductService productService(OrderService orderService1, OrderService orderService2) {
		System.out.println("构造方法1被调用了");
		return new ProductService();
	}

	/**
	 * 如果有多个@Bean注解创建同一bean对象
	 * 1, 如果同时存在静态和非静态的构造方法, 优先选用非静态的
	 * 2, 如果存在多个非静态的, 选择参数最多的一个
	 * AppConfig会设置为beanDefinition中的beanFactoryName属性值
	 * productService会设置为beanDefinition中的beanFactoryMethodName属性值
	 * @param orderService 参数入参完成自动注入
	 * @return
	 */
	@Bean
	public ProductService productService(OrderService orderService) {
		System.out.println("构造方法2被调用了");
		ProductService productService = new ProductService();
		productService.setOrderService(orderService);
		return productService;
	}

//	/**
//	 * spring中用于事件监听的功能
//	 * @return 返回一个事件监听器
//	 */
//	@Bean
//	private ApplicationListener applicationListener() {
//		return event -> System.out.println("接收到一个事件:" + event.getSource());
//	}

	/**
	 * spring中定义将字符串转换为对象属性值的功能
	 * @return
	 */
	@Bean
	private CustomEditorConfigurer customEditorConfigurer() {
		CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
		Map<Class<?>, Class<? extends PropertyEditor>> propertyEditorMap = new HashMap<>();
		propertyEditorMap.put(User.class, StringToUserPropertyEditor.class);
		customEditorConfigurer.setCustomEditors(propertyEditorMap);
		return customEditorConfigurer;
	}

	@Bean
	private ConversionServiceFactoryBean conversionService() {
		ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
		conversionServiceFactoryBean.setConverters(Collections.singleton(new StringToUserConverter()));
		return conversionServiceFactoryBean;
	}

//	@Bean
//	public JdbcTemplate jdbcTemplate() {
//		return new JdbcTemplate(dataSource());
//	}
//
//	@Bean
//	public PlatformTransactionManager transactionManager() {
//		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
//		transactionManager.setDataSource(dataSource());
//		return transactionManager;
//	}
//
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl("jdbc:mysql://localhost:3306/db_test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}



	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
		return sessionFactoryBean.getObject();
	}
//
//	@Bean
//	public SqlSessionTemplate sqlSession() throws Exception {
//		return new SqlSessionTemplate(sqlSessionFactory());
//	}

}

