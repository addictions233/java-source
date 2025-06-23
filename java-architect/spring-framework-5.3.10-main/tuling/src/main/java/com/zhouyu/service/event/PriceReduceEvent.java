package com.zhouyu.service.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author one
 * @description TODO
 * @date 2023-12-14
 */
public class PriceReduceEvent extends ApplicationEvent {

	public PriceReduceEvent(Object source) {
		super(source);
	}
}
