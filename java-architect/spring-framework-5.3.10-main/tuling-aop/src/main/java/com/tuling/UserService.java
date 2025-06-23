package com.tuling;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;

/**
 * @author 周瑜
 */
@Service
public class UserService implements UserInterface {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Resource
	private UserService userService;

	@Override
	public void test() {
//		Object proxy = AopContext.currentProxy();
//		System.out.println("当前代理对象:" +  proxy);
		System.out.println("test...");
		// 模拟自己调用aop失效的场景
//		this.test2("aaa");
		// 要解决自调用aop失效的问题, 有两种解决方式:
		// 1, bean对象自己注入自己
		// 2, 设施exposeProxy为true,然后从AopContext获取当前代理对象
//		UserInterface userInterface = (UserInterface) AopContext.currentProxy();
//		userInterface.test2("aaa");
	}

	@Override
	public void test2(String s) {
		System.out.println("test args...");
//		throw new NullPointerException();
	}

	/**
	 * 如果是同一个事务要么同时提交,同时回滚
	 * 如果是多个事务, 要对每个事务单独分析是提交还是回滚
	 */
	@Transactional
	public void insert() {
		// 定义事务挂起, 提交前, 回滚的回调函数
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void suspend() {
				TransactionSynchronization.super.suspend();
			}

			@Override
			public void resume() {
				TransactionSynchronization.super.resume();
			}

			@Override
			public void beforeCommit(boolean readOnly) {
				TransactionSynchronization.super.beforeCommit(readOnly);
			}

			@Override
			public void afterCommit() {
				TransactionSynchronization.super.afterCommit();
			}
		});
		jdbcTemplate.execute("INSERT INTO `tb_user`(`name`, `age`, `birthday`) VALUES ('123', 23, '1995-03-09')");
		try {
			userService.insert2();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Transactional
	public void insert2() {
		jdbcTemplate.execute("INSERT INTO `tb_user`(`name`, `age`, `birthday`) VALUES ('456', 23, '1995-03-09')");
		throw new NullPointerException();
	}


}
