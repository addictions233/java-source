package com.zhouyu.listener;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author one
 * @description springboot启动时在不同的阶段会发布一些事件
 * @date 2024-9-8
 */
public class MyApplicationEventListener implements ApplicationListener<SpringApplicationEvent> {
	@Override
	public void onApplicationEvent(SpringApplicationEvent event) {
		if (event instanceof ApplicationStartingEvent) {
			System.out.println("监听到了springboot启动事件");
		}

	}
}
