package com.one;

import com.one.spi.PaymentAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 使用Spring提供的SPI机制: SpringFactoriesLoader
 * 作为第三方jar包中类要想被扫描, 需要在spring.factories中配置, 然后被SpringFactoriesLoader加载
 */
@Configuration
public class SpiAutoConfiguration {

    /**
     * 收集所有的 PaymentAdapter bean, 构建注册表
     * @param adapterList 所有的PaymentAdapter
     * @return PaymentAdapterRegistry
     */
    @Bean
    public PaymentAdapterRegistry paymentAdapterRegistry(@Autowired List<PaymentAdapter> adapterList) {
        PaymentAdapterRegistry registry = new PaymentAdapterRegistry();
        adapterList.forEach(registry::register);
        return registry;
    }


    /**
     * PaymentAdapter 的注册表
     */
    public class PaymentAdapterRegistry {
        private final Map<String, PaymentAdapter> adapterMap = new ConcurrentHashMap<>();

        public PaymentAdapter getByType(String type) {
            if (adapterMap.containsKey(type)) {
                return adapterMap.get(type);
            }
            throw new IllegalArgumentException("No Adapter for type: " + type);
        }

        public void register(PaymentAdapter paymentAdapter) {
            adapterMap.put(paymentAdapter.getType(), paymentAdapter);
        }
    }
}
