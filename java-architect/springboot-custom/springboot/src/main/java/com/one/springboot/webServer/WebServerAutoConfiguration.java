package com.one.springboot.webServer;

import com.one.springboot.MyConditionalOnClass;
import com.one.springboot.configuaration.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author one
 * @description servlet容器自动配置
 * @date 2024-2-12
 */
@Configuration
public class WebServerAutoConfiguration implements AutoConfiguration {

    @Bean
    @MyConditionalOnClass("org.eclipse.jetty.server.Server")
    public JettyWebServer jettyWebServer(){
        return new JettyWebServer();
    }

    @Bean
    @MyConditionalOnClass("org.apache.catalina.startup.Tomcat")
    public TomcatWebServer tomcatWebServer(){
        return new TomcatWebServer();
    }
}
