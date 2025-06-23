package com.one.springboot;

import com.one.springboot.configuaration.AutoConfiguration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author one
 * @description 使用ImportSelector实现springboot自动配置, 实际上springboot读取spring.factories文件进行自动配置
 * @date 2024-2-13
 */
public class MyImportSelector implements DeferredImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        /**
         * 使用spi加载所有的配置类, 在META-INF/services目录下的配置
         */
        ServiceLoader<AutoConfiguration> serviceLoader = ServiceLoader.load(AutoConfiguration.class);
        List<String> autoConfigurations = new LinkedList<>();
        serviceLoader.forEach(autoConfiguration -> {
            String className = autoConfiguration.getClass().getName();
            autoConfigurations.add(className);
        });
        return autoConfigurations.toArray(new String[0]);
    }
}
