/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.dubbo;

import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.consumer.DemoServiceComponent;
import org.apache.dubbo.demo.DemoService;
import org.springframework.context.annotation.*;

import java.io.IOException;

/**
 * @author one
 * 启动dubbo服务端
 */
public class Application {
    /**
     * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before
     * launch the application
     */
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();
        DemoService service = context.getBean("demoServiceComponent", DemoServiceComponent.class);

        System.out.println("开始调用");
        String hello = service.sayHello("world");
        System.out.println("result :" + hello);


        System.in.read();
    }

    /**
     * 注解@EnableDubbo开启dubbo服务
     * 注解@PropertySource将dubbo的配置文件属性读取到ConsumerConfig对象中
     * 注解@ComponentScan扫描所有的dubbo服务端
     */
    @Configuration
    @EnableDubbo(scanBasePackages = "org.apache.dubbo.consumer")
    @PropertySource("classpath:/spring/dubbo-consumer.properties")
    @ComponentScan(value = {"org.apache.dubbo.consumer"})
    static class ConsumerConfiguration {

        @Bean
        public ConsumerConfig consumerConfig() {
            return new ConsumerConfig();
        }

    }
}
