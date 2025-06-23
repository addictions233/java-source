package com.tuling;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

@SPI
public interface Car {

    /**
     * 使用@Adaptive注解标明对接口中的哪个方法进行代理
     * 使用dubbo提供的adaptive代理方法中有URL参数
     */
    @Adaptive
    String getCarName(URL url);
}
