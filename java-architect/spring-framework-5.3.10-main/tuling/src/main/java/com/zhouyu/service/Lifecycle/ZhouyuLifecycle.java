package com.zhouyu.service.Lifecycle;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * @author one
 * @description 定义spring容器的生命周期函数
 * @date 2024-1-11
 */
@Component
public class ZhouyuLifecycle implements SmartLifecycle {
	@Override
	public void start() {
		System.out.println("ioc start success");
	}

	@Override
	public void stop() {
		System.out.println("ioc close");
	}

	@Override
	public boolean isRunning() {
		return true;
	}
}
