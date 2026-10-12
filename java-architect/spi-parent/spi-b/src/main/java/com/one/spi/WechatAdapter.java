package com.one.spi;

import com.one.entity.PayRequest;
import com.one.entity.PayResult;
import org.springframework.stereotype.Component;

@Component
public class WechatAdapter implements PaymentAdapter{
    @Override
    public String getType() {
        return "wechatPay";
    }

    @Override
    public PayResult pay(PayRequest request) {
        // 调用支付宝 SDK...
        return new PayResult(true, "ok");
    }
}
