package com.tuling.spi;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.springframework.web.WebApplicationInitializer;

/**
 * @author Fox
 */

@HandlesTypes(WebApplicationInitializer.class)
public class MySpringServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        System.out.println("=====onStartup=====");

    }
}
