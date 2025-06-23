package com.one.service;

import com.spring.*;

/**
 * @author one
 * @description 使用自定义注解创建bean对象
 * @date 2022-12-29
 */
@Component("userService")
@Scope("singleton")
public class UserService implements InitializingBean, BeanNameAware {

    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println("this is test method");
        System.out.println(orderService);
        System.out.println("beaName:" + beanName);
    }

    /**
     * 自动注入bean对象的名称
     */
    private String beanName;

    /**
     * Bean对象在初始化时会调用该方法
     */
    @Override
    public void afterPropertiesSet() {
        System.out.println("userService进行Bean对象的初始化中");
    }

    /**
     * 设置bean对象的名称
     *
     * @param name
     */
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
