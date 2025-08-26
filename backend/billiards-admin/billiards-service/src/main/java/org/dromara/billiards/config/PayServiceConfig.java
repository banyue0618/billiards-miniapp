package org.dromara.billiards.config;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.pay.config.WxPayV3Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付服务配置
 * 
 * @author zhangsip
 * @date 2024-07-02
 */
@Slf4j
@Configuration
public class PayServiceConfig {

    /**
     * 配置微信支付V3参数Bean，当模拟支付开启时不需要真实配置
     */
    @Bean
    @ConditionalOnProperty(name = "payment.mock-enabled", havingValue = "false")
    public WxPayV3Bean wxPayV3Bean() {
        log.info("初始化真实微信支付配置");
        return new WxPayV3Bean();
    }
    
    /**
     * 当开启模拟支付时，提供一个默认的WxPayV3Bean以避免空指针异常
     */
    @Bean
    @ConditionalOnProperty(name = "payment.mock-enabled", havingValue = "true", matchIfMissing = true)
    public WxPayV3Bean mockWxPayV3Bean() {
        log.info("初始化模拟微信支付配置");
        WxPayV3Bean bean = new WxPayV3Bean();
        bean.setAppId("mock-app-id");
        bean.setMchId("mock-mch-id");
        bean.setApiKey3("mock-api-key");
        bean.setCertPath("mock-cert-path");
        bean.setKeyPath("mock-key-path");
        bean.setPlatformCertPath("mock-platform-cert-path");
        bean.setDomain("http://localhost:8080");
        return bean;
    }
} 