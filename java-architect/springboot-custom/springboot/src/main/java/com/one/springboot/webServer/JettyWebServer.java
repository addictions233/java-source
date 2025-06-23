package com.one.springboot.webServer;

/**
 * @author one
 * @description JettyWebServer
 * @date 2024-2-12
 */
public class JettyWebServer implements WebServer{
    @Override
    public void start() {
        System.out.println("启动jetty web容器");
    }

    @Override
    public void stop() {

    }
}
