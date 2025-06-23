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

package org.apache.dubbo.consumer;

import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.demo.DemoService;

import org.springframework.stereotype.Component;

@Component("demoServiceComponent")
public class DemoServiceComponent implements DemoService {

    /**
     * 引用服务提供者provider, 这里注入的是一个ReferenceBean类型的代理对象, ReferenceBean是一个FactoryBean
     */
    @Reference
    private DemoService demoService;

    @Override
    public String sayHello(String name) {
        // Invoker
        return demoService.sayHello(name);
    }
}
