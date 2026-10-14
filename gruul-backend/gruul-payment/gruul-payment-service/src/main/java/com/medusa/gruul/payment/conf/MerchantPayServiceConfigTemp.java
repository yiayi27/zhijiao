package com.medusa.gruul.payment.conf;

import com.egzosn.pay.spring.boot.core.PayServiceConfigurer;
import com.egzosn.pay.spring.boot.core.configurers.MerchantDetailsServiceConfigurer;
import com.egzosn.pay.spring.boot.core.configurers.PayMessageConfigurer;
import com.egzosn.pay.spring.boot.core.merchant.PaymentPlatform;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.AliPaymentPlatform;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.PaymentPlatforms;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.WxPaymentPlatform;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.WxV3PaymentPlatform;
import com.medusa.gruul.payment.handler.notify.AliPayMessageHandler;
import com.medusa.gruul.payment.handler.notify.WxPayMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author 张治保
 * date 2024/12/15
 */
@RequiredArgsConstructor
public class MerchantPayServiceConfigTemp implements PayServiceConfigurer {

    private final AliPayMessageHandler aliPayMessageHandler;
    private final WxPayMessageHandler wxPayMessageHandler;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 商户配置
     *
     * @param merchants 商户配置
     */
    @Override
    public void configure(MerchantDetailsServiceConfigurer merchants) {
        merchants.jdbc()
                .cache(false)
                .template(jdbcTemplate);
    }

    /**
     * 商户配置
     *
     * @param configurer 支付消息配置
     */
    @Override
    public void configure(PayMessageConfigurer configurer) {

        PaymentPlatform aliPaymentPlatform = PaymentPlatforms.getPaymentPlatform(AliPaymentPlatform.PLATFORM_NAME);
        configurer.addHandler(aliPaymentPlatform, aliPayMessageHandler);

        PaymentPlatform wechatPaymentPlatform = PaymentPlatforms.getPaymentPlatform(WxPaymentPlatform.PLATFORM_NAME);
        configurer.addHandler(wechatPaymentPlatform, wxPayMessageHandler);

        PaymentPlatform wxV3PaymentPlatform = PaymentPlatforms.getPaymentPlatform(WxV3PaymentPlatform.PLATFORM_NAME);
        configurer.addHandler(wxV3PaymentPlatform, wxPayMessageHandler);
    }
}
