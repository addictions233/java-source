package com.tuling;

public class BlackPerson implements Person {

    /**
     * 使用dubbo的spi加载会自动进行依赖注入
     * dubbo最开始赋值的是 一个Adaptive (AOP代理对象)
     */
    private Car car;

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public Car getCar() {
        return car;
    }
}
