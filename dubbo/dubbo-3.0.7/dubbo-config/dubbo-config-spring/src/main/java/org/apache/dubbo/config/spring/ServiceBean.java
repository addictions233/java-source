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
package org.apache.dubbo.config.spring;

import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.config.spring.context.event.ServiceBeanExportedEvent;
import org.apache.dubbo.config.spring.util.DubboBeanUtils;
import org.apache.dubbo.config.support.Parameter;
import org.apache.dubbo.rpc.model.ModuleModel;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * ServiceFactoryBean
 *
 * @export
 */
public class ServiceBean<T> extends ServiceConfig<T> implements InitializingBean, DisposableBean,
        ApplicationContextAware, BeanNameAware, ApplicationEventPublisherAware {


    private static final long serialVersionUID = 213195494150089726L;

    /**
     * 使用transient不会被序列化
     */
    private final transient Service service;

    private transient ApplicationContext applicationContext;

    private transient String beanName;

    private ApplicationEventPublisher applicationEventPublisher;

    public ServiceBean() {
        super();
        this.service = null;
    }

    public ServiceBean(ModuleModel moduleModel) {
        super(moduleModel);
        this.service = null;
    }

    public ServiceBean(Service service) {
        super(service);
        this.service = service;
    }

    public ServiceBean(ModuleModel moduleModel, Service service) {
        super(moduleModel, service);
        this.service = service;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    /**
     * Gets associated {@link Service}
     *
     * @return associated {@link Service}
     */
    public Service getService() {
        return service;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(getPath())) {
            if (StringUtils.isNotEmpty(getInterface())) {
                setPath(getInterface());
            }
        }
        //register service bean
        ModuleModel moduleModel = DubboBeanUtils.getModuleModel(applicationContext);
        moduleModel.getConfigManager().addService(this);
        moduleModel.getDeployer().setPending();
    }

    /**
     * Get the name of {@link ServiceBean}
     *
     * @return {@link ServiceBean}'s name
     * @since 2.6.5
     */
    @Parameter(excluded = true, attribute = false)
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * 单个ServiceBean的服务导出
     * @since 2.6.5
     */
    @Override
    protected void exported() {
        // 进行服务导出
        super.exported();

        // Publish ServiceBeanExportedEvent
        // 发出导出事件
        publishExportEvent();
    }

    /**
     * @since 2.6.5
     */
    private void publishExportEvent() {
        ServiceBeanExportedEvent exportEvent = new ServiceBeanExportedEvent(this);
        applicationEventPublisher.publishEvent(exportEvent);
    }

    @Override
    public void destroy() throws Exception {
        // no need to call unexport() here, see
        // org.apache.dubbo.config.spring.extension.SpringExtensionInjector.ShutdownHookListener
    }

    // merged from dubbox
    @Override
    protected Class getServiceClass(T ref) {
        if (AopUtils.isAopProxy(ref)) {
            return AopUtils.getTargetClass(ref);
        }
        return super.getServiceClass(ref);
    }

    /**
     * @param applicationEventPublisher
     * @since 2.6.5
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
