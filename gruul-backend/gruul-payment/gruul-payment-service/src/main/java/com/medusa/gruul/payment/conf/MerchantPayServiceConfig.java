package com.medusa.gruul.payment.conf;

import com.egzosn.pay.spring.boot.autoconfigue.PayAutoConfiguration;
import com.egzosn.pay.spring.boot.core.PayServiceConfigurer;
import com.egzosn.pay.spring.boot.core.builders.MerchantDetailsServiceBuilder;
import com.egzosn.pay.spring.boot.core.configurers.MerchantDetailsServiceConfigurer;
import com.egzosn.pay.spring.boot.core.configurers.PayMessageConfigurer;
import com.egzosn.pay.spring.boot.core.merchant.MerchantDetailsService;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.AliPaymentPlatform;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.PaymentPlatforms;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.WxV3PaymentPlatform;
import com.medusa.gruul.payment.handler.notify.AliPayMessageHandler;
import com.medusa.gruul.payment.handler.notify.WxPayMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * 支付服务配置
 *
 * @author xiaoq
 * @see PayAutoConfiguration
 * @since 2022-07-12 16:47
 */
@Configuration
@RequiredArgsConstructor
public class MerchantPayServiceConfig implements PayServiceConfigurer {

    private final AliPayMessageHandler aliPayMessageHandler;
    private final WxPayMessageHandler wxPayMessageHandler;


    @Override
    public void configure(MerchantDetailsServiceConfigurer configurer) {
        //使用redis 缓存配置
        configurer.initBuilder(new MerchantDetailsServiceBuilder() {
            @Override
            protected MerchantDetailsService<?> performBuild() {
                return RedisMerchantPayConfigManager.instance;
            }
        });
    }

    @Override
    public void configure(PayMessageConfigurer configurer) {
        RedisMerchantPayConfigManager.instance.setPayMessageConfigurer(configurer);
        configurer.addHandler(
                PaymentPlatforms.getPaymentPlatform(AliPaymentPlatform.PLATFORM_NAME),
                aliPayMessageHandler
        );
        configurer.addHandler(
                PaymentPlatforms.getPaymentPlatform(WxV3PaymentPlatform.PLATFORM_NAME),
                wxPayMessageHandler
        );
    }
}
