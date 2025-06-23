package com.one.io;

import java.io.InputStream;

/**
 * @ClassName: Resources
 * @Description: 加载配置文件
 * @Author: one
 * @Date: 2022/03/16
 */
public class Resources {
    /**
     * mybatis用来加载配置文件的工具类,获取流对象
     * @param resource 资源路径
     * @return InputStream
     */
    public static InputStream getResourceAsStream(String resource) {
        return Resources.class.getClassLoader().getResourceAsStream(resource);
    }
}
