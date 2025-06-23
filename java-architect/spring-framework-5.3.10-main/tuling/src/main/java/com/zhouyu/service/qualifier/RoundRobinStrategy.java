package com.zhouyu.service.qualifier;

import org.springframework.stereotype.Component;

/**
 * @author one
 * @description TODO
 * @date 2023-12-21
 */
@Component
@RoundRobin
public class RoundRobinStrategy implements LoadBalance{
}
