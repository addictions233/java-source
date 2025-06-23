package com.one;

import com.one.service.OrderService;
import com.one.service.UserService;
import com.spring.MyApplicationContext;

/**
 * @author one
 * @description 测试spring-ioc容器的使用
 * @date 2022-12-27
 */
public class Main {
    public static void main(String[] args) {
        MyApplicationContext applicationContext = new MyApplicationContext(AppConfig.class);

        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();


        OrderService orderService = (OrderService) applicationContext.getBean("orderService");
        System.out.println(orderService);
    }
}
