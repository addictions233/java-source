package com.one;

import com.one.dao.IUserDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ServiceLoader;

/**
 * @author one
 * @description spi测试启动类
 * @date 2024-1-27
 */
@SpringBootApplication(scanBasePackages = "com.one")
public class App {

    public static void main(String[] args) {
        // 使用JDK自带的SPI机制: ServiceLoader
        // META-INF/services是Java原生SPI的固定目录
        ServiceLoader<IUserDao> userDaoList = ServiceLoader.load(IUserDao.class);
        for (IUserDao userDao : userDaoList) {
            userDao.saveUser();
        }

        // 使用 Spring 提供的SPI机制: SpringFactoriesLoader
        // SpringFactoriesLoader通过classLoader.getResources("META-INF/spring.factories")
        // 一次性扫描classpath下所有jar包中的该文件
        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
        SpiAutoConfiguration.PaymentAdapterRegistry registry = context.getBean("paymentAdapterRegistry", SpiAutoConfiguration.PaymentAdapterRegistry.class);
        System.out.println(registry.getByType("alipay"));
        System.out.println(registry.getByType("wechatPay"));
    }
}
