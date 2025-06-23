package com.tuling.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author one
 * @description TODO
 * @date 2024-3-15
 */
@ComponentScan("com.tuling")
@EnableTransactionManagement(proxyTargetClass = true)
public class TransactionAppConfig {

	/**
	 * JDBC事务管理的基础是Connection,一般项目中都使用数据库连接池, 从dataSource中获取 connection
	 * 所以要让spring接管事务, 必须让JdbcTemplate,PlatformTransactionManager使用同一个dataSource数据源
	 * @return DataSource
	 */

	@Bean
	private DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/tuling?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;

	}

	/**
	 * 如果要想spring能够管理事务, 必须让JdbcTemplate,PlatformTransactionManager使用同一个dataSource数据源
	 */
	@Bean
	public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public PlatformTransactionManager transactionManager(@Autowired DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

	/**
	 * TransactionTemplate 是spring提供的一个事务模板类, 可以简化编程式事务的代码, 它继承了DefaultTransactionDefinition
	 */
	@Bean
	public TransactionTemplate transactionTemplate(@Autowired PlatformTransactionManager transactionManager) {
		return new TransactionTemplate(transactionManager);
	}







}
