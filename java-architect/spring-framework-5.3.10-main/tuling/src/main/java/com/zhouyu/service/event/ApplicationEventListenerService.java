package com.zhouyu.service.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author one
 * @description TODO
 * @date 2023-12-14
 */
@Component
public class ApplicationEventListenerService implements ApplicationListener<PriceReduceEvent> {

	@Override
	public void onApplicationEvent(PriceReduceEvent priceReduceEvent) {
		Object source = priceReduceEvent.getSource();
		System.out.println("接收到事件:" + source);
	}

//	@EventListener
//	public void onApplicationEvent2(PriceReduceEvent priceReduceEvent) {
//		Object source = priceReduceEvent.getSource();
//		System.out.println("接收到事件2:" + source);
//	}
}
