package com.zhouyu.listener;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;

/**
 * @author one
 * @description 自定义容器启动过程中的各个阶段的回调函数
 * @date 2024-9-7
 */
public class MyApplicationRunListener implements SpringApplicationRunListener, Ordered {

	private final SpringApplication application;

	private final String[] args;

	public MyApplicationRunListener(SpringApplication application, String[] args) {
		this.application = application;
		this.args = args;
	}

	@Override
	public void starting(ConfigurableBootstrapContext bootstrapContext) {
		System.out.println("容器启动了");
	}

	@Override
	public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
		System.out.println("准备环境变量");
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
		System.out.println("容器准备");
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		System.out.println("容器加载");
	}

	@Override
	public void started(ConfigurableApplicationContext context, Duration timeTaken) {
		System.out.println("已经启动了");
	}

	@Override
	public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
		System.out.println("准备好了");
	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
		System.out.println("准备环境变量");
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
