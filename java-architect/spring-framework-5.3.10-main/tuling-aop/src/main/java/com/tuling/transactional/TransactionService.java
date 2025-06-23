package com.tuling.transactional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;

/**
 * @author one
 * @description 使用spring编程式事务
 * @date 2025-3-16
 */
@Service
public class TransactionService {

	@Resource
	private DataSourceTransactionManager transactionManager;

	@Resource
	private TransactionDefinition transactionDefinition;

	/**
	 * 如果要想spring能够管理事务, 必须让JdbcTemplate,PlatformTransactionManager使用同一个dataSource数据源
	 */
	@Resource
	private JdbcTemplate jdbcTemplate;


	public void test() {
		TransactionStatus  transactionStatus = null;
		try {
			// 使用平台事务管理器开启事务
			transactionStatus = transactionManager.getTransaction(transactionDefinition);
			// 执行业务数据
			System.out.println("test run...");
			jdbcTemplate.update("insert into t_user (name) values (?)","one");
			int i = 1/0;
			jdbcTemplate.update("insert into t_user (name) values (?)","two");
		} catch (Exception e) {
			// 回滚事务
			if (transactionStatus != null && !transactionStatus.isCompleted()) {
				transactionManager.rollback(transactionStatus);
			}
			e.printStackTrace();
		} finally {
			if (transactionStatus != null && !transactionStatus.isCompleted()) {
				transactionManager.commit(transactionStatus);
			}
		}
	}
}
