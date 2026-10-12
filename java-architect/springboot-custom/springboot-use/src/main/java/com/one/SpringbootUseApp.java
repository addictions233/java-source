package com.one;

import com.one.springboot.MyImportSelector;
import com.one.springboot.MySpringBootApplication;
import com.one.springboot.webServer.WebServer;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * @author one
 * @description 启动类
 * @date 2024-2-10
 */
@MySpringBootApplication
//@Import(WebServerAutoConfiguration.class)
@Import(MyImportSelector.class) // 使用@Import创建第三方jar的 MyImportSelector 类创建Bean对象
public class SpringbootUseApp {

    public static void main(String[] args) {
        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.register(SpringbootUseApp.class);
        webApplicationContext.refresh();

        // 启动web Server
        WebServer webServer = getWebServer(webApplicationContext);
        webServer.start();
    }

    private static WebServer getWebServer(AnnotationConfigWebApplicationContext webApplicationContext) {
        return webApplicationContext.getBean(WebServer.class);
    }


}
