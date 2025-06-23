/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config.spring.context.annotation;

import org.apache.dubbo.config.AbstractConfig;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import static org.apache.dubbo.config.spring.util.AnnotatedBeanDefinitionRegistryUtils.registerBeans;

/**
 * Dubbo {@link AbstractConfig Config} {@link ImportBeanDefinitionRegistrar register}, which order can be configured
 *
 * @see EnableDubboConfig
 * @see DubboConfigConfiguration
 * @see Ordered
 * @since 2.5.8
 */
public class DubboConfigConfigurationRegistrar implements ImportBeanDefinitionRegistrar {


    /**
     * k1 处理dubbo中的配置文件生成对应的配置对象的方法入口
     * 注册bean对象来读取dubbo的properties配置文件
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        System.out.println("执行DubboConfigConfigurationRegistrar");

        // 获取@EnableDubboConfig注解的属性值
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableDubboConfig.class.getName()));

        // 默认为true
        boolean multiple = attributes.getBoolean("multiple");

        // Single Config Bindings
        // 注册DubboConfigConfiguration.Single的Bean对象
        registerBeans(registry, DubboConfigConfiguration.Single.class);

        // Since 2.6.6 https://github.com/apache/dubbo/issues/3193
        if (multiple) {
            // 注册DubboConfigConfiguration.Multiple的Bean对象
            registerBeans(registry, DubboConfigConfiguration.Multiple.class);
        }
    }

}
