package com.one.springboot.webServer;

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author one
 * @description TomcatWebServer
 * @date 2024-2-12
 */
public class TomcatWebServer implements WebServer, ApplicationContextAware {
    private WebApplicationContext webApplicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.webApplicationContext = (WebApplicationContext) applicationContext;
    }

    /**
     * 启动tomcat
     *
     */
    @Override
    public void start() {
        System.out.println("启动tomcat web容器");
        Tomcat tomcat = new Tomcat();

        Server server = tomcat.getServer();
        Service service = server.findService("Tomcat");

        Connector connector = new Connector();
        connector.setPort(8081);

        Engine engine = new StandardEngine();
        engine.setDefaultHost("localhost");

        Host host = new StandardHost();
        host.setName("localhost");

        String contextPath = "";
        Context context = new StandardContext();
        context.setPath(contextPath);
        context.addLifecycleListener(new Tomcat.FixContextListener());

        host.addChild(context);
        engine.addChild(host);

        service.setContainer(engine);
        service.addConnector(connector);
        // 向tomcat中添加一个servlet: dispatcherServlet
        // dispatcherServlet中的构造参数需要一个web容器
        tomcat.addServlet(contextPath, "dispatcher", new DispatcherServlet(this.webApplicationContext));
        // /* 表示dispatcherServlet接管所有的路径请求
        context.addServletMappingDecoded("/*", "dispatcher");


        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void stop() {

    }


}
