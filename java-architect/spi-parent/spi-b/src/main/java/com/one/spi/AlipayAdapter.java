package com.one.spi;

import com.one.entity.PayRequest;
import com.one.entity.PayResult;
import org.springframework.stereotype.Component;

// 关键：实现类必须被 Spring 扫描到
// 用户把实现类放在被 @SpringBootApplication 扫描的包下（或加 @ComponentScan 指定包）
@Component
public class AlipayAdapter implements PaymentAdapter {

    @Override
    public String getType() {
        return "alipay";
    }

    @Override
    public PayResult pay(PayRequest request) {
        // 调用支付宝 SDK...
        return new PayResult(true, "ok");
    }
}