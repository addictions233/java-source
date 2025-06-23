package com.one.springboot.webServer;

/**
 * web servlet容器启动接口
 */
public interface WebServer {

    /**
     * 启动web容器
     */
    void start();

    /**
     * 关闭web容器
     */
    void stop();
}
