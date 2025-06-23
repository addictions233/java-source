package com.one.mvcframework.v2.servlet;

import com.one.mvcframework.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: DispatcherServlet
 * @Description: 手写DispatcherServlet的v2版本
 * @Author: one
 * @Date: 2022/05/05
 */
public class DispatcherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }


    @Override
    public void init() throws ServletException {

    }

    /**
     * 模拟springmvc中的HandleMapping
     */
    public class HandlerMapping {
        /**
         * handler处理器的url
         */
        private String url;

        /**
         * handler处理器的方法
         */
        private Method handlerMethod;

        /**
         * 处理器handler所在的controller
         */
        private Object controller;

        /**
         * 存在处理器方法的 参数名: index位置, key为参数字段名称, value是其所在的位置索引
         */
        private Map<String, Integer> paramIndexMapping;

        /**
         * 构造方法
         *
         * @param url
         * @param handlerMethod
         * @param controller
         */
        public HandlerMapping(String url, Method handlerMethod, Object controller) {
            this.url = url;
            this.handlerMethod = handlerMethod;
            this.controller = controller;

            paramIndexMapping = new HashMap<>();
            putParamIndex(handlerMethod);
        }

        /**
         * 参数处理
         *
         * @param handlerMethod 处理方法
         */
        private void putParamIndex(Method handlerMethod) {
            Parameter[] parameters = handlerMethod.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Class<?> type = parameter.getType();
                if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                    paramIndexMapping.put(type.getName(), i);
                } else {
                    if (parameter.isAnnotationPresent(RequestParam.class)) {
                        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                        paramIndexMapping.put(annotation.value(),i);
                    }
                }
            }
        }
    }
}
