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
package org.apache.dubbo.config.spring.beans.factory.annotation;

import java.util.Collections;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.ArrayUtils;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.config.spring.context.annotation.DubboClassPathBeanDefinitionScanner;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.dubbo.config.spring.beans.factory.annotation.ServiceBeanNameBuilder.create;
import static org.apache.dubbo.config.spring.util.AnnotationUtils.resolveServiceInterfaceClass;
import static org.apache.dubbo.config.spring.util.ObjectUtils.of;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 * {@link Service} Annotation
 * {@link BeanDefinitionRegistryPostProcessor Bean Definition Registry Post Processor}
 *
 * @since 2.5.8
 */
public class ServiceAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Set<String> packagesToScan;

    private Environment environment;

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    public ServiceAnnotationBeanPostProcessor(String... packagesToScan) {
        this(Arrays.asList(packagesToScan));
    }

    public ServiceAnnotationBeanPostProcessor(Collection<String> packagesToScan) {
        this(new LinkedHashSet<>(packagesToScan));
    }

    public ServiceAnnotationBeanPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 获取扫描路径
        Set<String> resolvedPackagesToScan = resolvePackagesToScan(packagesToScan);

        if (!CollectionUtils.isEmpty(resolvedPackagesToScan)) {
            // 扫描包，进行Bean注册, 处理dubbo中的@Service注解
            registerServiceBeans(resolvedPackagesToScan, registry);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("packagesToScan is empty , ServiceBean registry will be ignored!");
            }
        }

    }


    /**
     * Registers Beans whose classes was annotated {@link Service}
     *
     * @param packagesToScan The base packages to scan
     * @param registry       {@link BeanDefinitionRegistry}
     */
    private void registerServiceBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        // 构建扫描器
        DubboClassPathBeanDefinitionScanner scanner =
                new DubboClassPathBeanDefinitionScanner(registry, environment, resourceLoader);

        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);

        scanner.setBeanNameGenerator(beanNameGenerator);

        // 扫描被Service注解标注的类
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));

        /**
         * Add the compatibility for legacy Dubbo's @Service
         *
         * The issue : https://github.com/apache/dubbo/issues/4330
         * @since 2.7.3
         */
        scanner.addIncludeFilter(new AnnotationTypeFilter(com.alibaba.dubbo.config.annotation.Service.class));

        for (String packageToScan : packagesToScan) {

            // Registers @Service Bean first
            // 扫描Dubbo自定义的@Service注解
            scanner.scan(packageToScan);

            // 查找被@Service注解的类的BeanDefinition（无论这个类有没有被@ComponentScan注解标注了）
            // Finds all BeanDefinitionHolders of @Service whether @ComponentScan scans or not.
            Set<BeanDefinitionHolder> beanDefinitionHolders =
                    findServiceBeanDefinitionHolders(scanner, packageToScan, registry, beanNameGenerator);

            if (!CollectionUtils.isEmpty(beanDefinitionHolders)) {

                // 扫描到BeanDefinition开始处理它
                for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                    // 重点: 注册@Serivce注解修饰的Bean
                    registerServiceBean(beanDefinitionHolder, registry, scanner);
                }

                if (logger.isInfoEnabled()) {
                    logger.info(beanDefinitionHolders.size() + " annotated Dubbo's @Service Components { " +
                            beanDefinitionHolders +
                            " } were scanned under package[" + packageToScan + "]");
                }

            } else {

                if (logger.isWarnEnabled()) {
                    logger.warn("No Spring Bean annotating Dubbo's @Service was found under package["
                            + packageToScan + "]");
                }

            }

        }

    }

    /**
     * It'd better to use BeanNameGenerator instance that should reference
     * {@link ConfigurationClassPostProcessor#componentScanBeanNameGenerator},
     * thus it maybe a potential problem on bean name generation.
     *
     * @param registry {@link BeanDefinitionRegistry}
     * @return {@link BeanNameGenerator} instance
     * @see SingletonBeanRegistry
     * @see AnnotationConfigUtils#CONFIGURATION_BEAN_NAME_GENERATOR
     * @see ConfigurationClassPostProcessor#processConfigBeanDefinitions
     * @since 2.5.8
     */
    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {

        BeanNameGenerator beanNameGenerator = null;

        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }

        if (beanNameGenerator == null) {

            if (logger.isInfoEnabled()) {

                logger.info("BeanNameGenerator bean can't be found in BeanFactory with name ["
                        + CONFIGURATION_BEAN_NAME_GENERATOR + "]");
                logger.info("BeanNameGenerator will be a instance of " +
                        AnnotationBeanNameGenerator.class.getName() +
                        " , it maybe a potential problem on bean name generation.");
            }

            beanNameGenerator = new AnnotationBeanNameGenerator();

        }

        return beanNameGenerator;

    }

    /**
     * Finds a {@link Set} of {@link BeanDefinitionHolder BeanDefinitionHolders} whose bean type annotated
     * {@link Service} Annotation.
     *
     * @param scanner       {@link ClassPathBeanDefinitionScanner}
     * @param packageToScan pachage to scan
     * @param registry      {@link BeanDefinitionRegistry}
     * @return non-null
     * @since 2.5.8
     */
    private Set<BeanDefinitionHolder> findServiceBeanDefinitionHolders(
            ClassPathBeanDefinitionScanner scanner, String packageToScan, BeanDefinitionRegistry registry,
            BeanNameGenerator beanNameGenerator) {

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);

        Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<>(beanDefinitions.size());

        for (BeanDefinition beanDefinition : beanDefinitions) {

            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            beanDefinitionHolders.add(beanDefinitionHolder);

        }

        return beanDefinitionHolders;

    }

    /**
     * Registers {@link ServiceBean} from new annotated {@link Service} {@link BeanDefinition}
     *
     * @param beanDefinitionHolder
     * @param registry
     * @param scanner
     * @see ServiceBean
     * @see BeanDefinition
     */
    private void registerServiceBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry,
                                     DubboClassPathBeanDefinitionScanner scanner) {
        // 处理扫描到的每一个BeanDefinition
        // 1. 得到@Service注解上所配置的参数
        // 2. 根据每一个BeanDefinition会再额外的生成一个ServiceBean
        // 3. 对于每一个被@Service注解的类（服务的实现类），会生成两个Bean，一个服务实现类对应的Bean（普通Bean，和@Component一样），一个ServiceBean（Dubbo中要用到的Bean，因为在ServiceBean中包括了很的Config）

        // 具体的服务实现类
        Class<?> beanClass = resolveClass(beanDefinitionHolder);
        // @Service可以对服务进行各种配置
        Annotation service = findServiceAnnotation(beanClass);

        /**
         * The {@link AnnotationAttributes} of @Service annotation
         */
        AnnotationAttributes serviceAnnotationAttributes = getAnnotationAttributes(service, false, false);

        // 服务实现类对应的接口
        Class<?> interfaceClass = resolveServiceInterfaceClass(serviceAnnotationAttributes, beanClass);
        // 服务实现类对应的bean的名字，比如：demoServiceImpl
        String annotatedServiceBeanName = beanDefinitionHolder.getBeanName();

        // 生成一个ServiceBean
        AbstractBeanDefinition serviceBeanDefinition =
                buildServiceBeanDefinition(service, serviceAnnotationAttributes, interfaceClass, annotatedServiceBeanName);

        // ServiceBean Bean name   ServiceBean表示服务，我们要使用一个服务应该拿ServiceBean
        String beanName = generateServiceBeanName(serviceAnnotationAttributes, interfaceClass);

        if (scanner.checkCandidate(beanName, serviceBeanDefinition)) { // check duplicated candidate bean

            // 把ServiceBean注册进去，对应的beanName为ServiceBean:org.apache.dubbo.demo.DemoService
            registry.registerBeanDefinition(beanName, serviceBeanDefinition);

            if (logger.isInfoEnabled()) {
                logger.info("The BeanDefinition[" + serviceBeanDefinition +
                        "] of ServiceBean has been registered with name : " + beanName);
            }

        } else {

            if (logger.isWarnEnabled()) {
                logger.warn("The Duplicated BeanDefinition[" + serviceBeanDefinition +
                        "] of ServiceBean[ bean name : " + beanName +
                        "] was be found , Did @DubboComponentScan scan to same package in many times?");
            }

        }

    }


    /**
     * Find the {@link Annotation annotation} of @Service
     *
     * @param beanClass the {@link Class class} of Bean
     * @return <code>null</code> if not found
     * @since 2.7.3
     */
    private Annotation findServiceAnnotation(Class<?> beanClass) {
        Annotation service = findMergedAnnotation(beanClass, Service.class);
        if (service == null) {
            service = findMergedAnnotation(beanClass, com.alibaba.dubbo.config.annotation.Service.class);
        }
        return service;
    }

    /**
     * Generates the bean name of {@link ServiceBean}
     *
     * @param serviceAnnotationAttributes
     * @param interfaceClass              the class of interface annotated {@link Service}
     * @return ServiceBean@interfaceClassName#annotatedServiceBeanName
     * @since 2.7.3
     */
    private String generateServiceBeanName(AnnotationAttributes serviceAnnotationAttributes, Class<?> interfaceClass) {
        ServiceBeanNameBuilder builder = create(interfaceClass, environment)
                .group(serviceAnnotationAttributes.getString("group"))
                .version(serviceAnnotationAttributes.getString("version"));
        return builder.build();
    }

    private Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {

        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();

        return resolveClass(beanDefinition);

    }

    private Class<?> resolveClass(BeanDefinition beanDefinition) {

        String beanClassName = beanDefinition.getBeanClassName();

        return resolveClassName(beanClassName, classLoader);

    }

    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }

    /**
     * Build the {@link AbstractBeanDefinition Bean Definition}
     *
     * @param serviceAnnotation
     * @param serviceAnnotationAttributes
     * @param interfaceClass
     * @param annotatedServiceBeanName
     * @return
     * @since 2.7.3
     */
    private AbstractBeanDefinition buildServiceBeanDefinition(Annotation serviceAnnotation,
                                                              AnnotationAttributes serviceAnnotationAttributes,
                                                              Class<?> interfaceClass,
                                                              String annotatedServiceBeanName) {
        // 生成一个ServiceBean对应的BeanDefinition
        BeanDefinitionBuilder builder = rootBeanDefinition(ServiceBean.class);

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        // 忽略的属性名称
        String[] ignoreAttributeNames = of("provider", "monitor", "application", "module", "registry", "protocol",
                "interface", "interfaceName", "parameters");

        // 把serviceAnnotation中的参数值赋值给ServiceBean的属性
        propertyValues.addPropertyValues(new AnnotationPropertyValuesAdapter(serviceAnnotation, environment, ignoreAttributeNames));

        // References "ref" property to annotated-@Service Bean
        // ref属性赋值为另外一个bean, 对应的就是被@Service注解的服务实现类对应的bean
        addPropertyReference(builder, "ref", annotatedServiceBeanName);
        // Set interface
        builder.addPropertyValue("interface", interfaceClass.getName());
        // Convert parameters into map
        builder.addPropertyValue("parameters", convertParameters(serviceAnnotationAttributes.getStringArray("parameters")));

        // 配置了methods属性，则给ServiceBean对应的methods属性赋值
        // Add methods parameters
        List<MethodConfig> methodConfigs = convertMethodConfigs(serviceAnnotationAttributes.get("methods"));
        if (!methodConfigs.isEmpty()) {
            builder.addPropertyValue("methods", methodConfigs);
        }

        /**
         * Add {@link org.apache.dubbo.config.ProviderConfig} Bean reference
         */
        String providerConfigBeanName = serviceAnnotationAttributes.getString("provider");
        if (StringUtils.hasText(providerConfigBeanName)) {
            addPropertyReference(builder, "provider", providerConfigBeanName);
        }

        /**
         * Add {@link org.apache.dubbo.config.MonitorConfig} Bean reference
         */
        String monitorConfigBeanName = serviceAnnotationAttributes.getString("monitor");
        if (StringUtils.hasText(monitorConfigBeanName)) {
            addPropertyReference(builder, "monitor", monitorConfigBeanName);
        }

        /**
         * Add {@link org.apache.dubbo.config.ApplicationConfig} Bean reference
         */
        String applicationConfigBeanName = serviceAnnotationAttributes.getString("application");
        if (StringUtils.hasText(applicationConfigBeanName)) {
            addPropertyReference(builder, "application", applicationConfigBeanName);
        }

        /**
         * Add {@link org.apache.dubbo.config.ModuleConfig} Bean reference
         */
        String moduleConfigBeanName = serviceAnnotationAttributes.getString("module");
        if (StringUtils.hasText(moduleConfigBeanName)) {
            addPropertyReference(builder, "module", moduleConfigBeanName);
        }


        /**
         * Add {@link org.apache.dubbo.config.RegistryConfig} Bean reference
         * 获取注解上配置的注册中心的beanName
         */
        String[] registryConfigBeanNames = serviceAnnotationAttributes.getStringArray("registry");

        List<RuntimeBeanReference> registryRuntimeBeanReferences = toRuntimeBeanReferences(registryConfigBeanNames);

        if (!registryRuntimeBeanReferences.isEmpty()) {
            builder.addPropertyValue("registries", registryRuntimeBeanReferences);
        }

        /**
         * Add {@link org.apache.dubbo.config.ProtocolConfig} Bean reference
         */
        String[] protocolConfigBeanNames = serviceAnnotationAttributes.getStringArray("protocol");

        List<RuntimeBeanReference> protocolRuntimeBeanReferences = toRuntimeBeanReferences(protocolConfigBeanNames);

        if (!protocolRuntimeBeanReferences.isEmpty()) {
            builder.addPropertyValue("protocols", protocolRuntimeBeanReferences);
        }

        return builder.getBeanDefinition();

    }

    private List convertMethodConfigs(Object methodsAnnotation) {
        if (methodsAnnotation == null){
            return Collections.EMPTY_LIST;
        }
        return MethodConfig.constructMethodConfig((Method[])methodsAnnotation);
    }

    private ManagedList<RuntimeBeanReference> toRuntimeBeanReferences(String... beanNames) {

        ManagedList<RuntimeBeanReference> runtimeBeanReferences = new ManagedList<>();

        if (!ObjectUtils.isEmpty(beanNames)) {

            for (String beanName : beanNames) {

                String resolvedBeanName = environment.resolvePlaceholders(beanName);

                runtimeBeanReferences.add(new RuntimeBeanReference(resolvedBeanName));
            }

        }

        return runtimeBeanReferences;

    }

    private void addPropertyReference(BeanDefinitionBuilder builder, String propertyName, String beanName) {
        String resolvedBeanName = environment.resolvePlaceholders(beanName);
        builder.addPropertyReference(propertyName, resolvedBeanName);
    }


    private Map<String, String> convertParameters(String[] parameters) {
        if (ArrayUtils.isEmpty(parameters)) {
            return null;
        }

        if (parameters.length % 2 != 0) {
            throw new IllegalArgumentException("parameter attribute must be paired with key followed by value");
        }

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i += 2) {
            map.put(parameters[i], parameters[i + 1]);
        }
        return map;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
