package com.mallease.trade.config;

import com.alipay.api.AlipayConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝沙箱配置（公钥模式）
 *
 * @author: Aulen
 * @create: 2026-02-09
 */
@Configuration
public class AlipayConfiguration {

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.private-key}")
    private String privateKey;

    @Getter
    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    @Value("${alipay.gateway}")
    private String gateway;

    @Getter
    @Value("${alipay.notify-url}")
    private String notifyUrl;

    @Getter
    @Value("${alipay.return-url}")
    private String returnUrl;

    @Bean
    public AlipayConfig getAlipayConfig() {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(gateway);
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }
}
