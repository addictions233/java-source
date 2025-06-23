package com.tuling;

import org.apache.dubbo.common.URL;

/**
 * dubbo中对spi加载的对象允许通过装饰者模式进行包装, 生成包装wrapper对象
 */
public class CarWrapper implements Car {

    private Car car;

    /**
     * 如果有包装类, 则创建的是包装类对象
     * @param car
     */
    public CarWrapper(Car car) {
        this.car = car;
    }

    @Override
    public String getCarName(URL url) {
        System.out.println("wrapper...");
        return car.getCarName(url);
    }
}
