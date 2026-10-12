package com.one.spi;

import com.one.entity.PayRequest;
import com.one.entity.PayResult;

/**
 * 定义SPI接口
 */
public interface PaymentAdapter {

    /**
     * 适配器标识, 用于业务选择
     */
    String getType();

    /**
     * 业务方法
     */
    PayResult pay(PayRequest request);
}
