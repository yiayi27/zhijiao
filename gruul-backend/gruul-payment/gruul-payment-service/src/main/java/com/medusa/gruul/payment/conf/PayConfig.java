package com.medusa.gruul.payment.conf;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.medusa.gruul.payment.api.enums.PayChannelEnum;
import com.medusa.gruul.payment.api.enums.WxPayEnum;
import com.medusa.gruul.payment.handler.WxPayMiniHandler;
import com.medusa.gruul.payment.router.PayMessageRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author whh
 */
@Component
public class PayConfig {

    @Autowired
    private WxPayMiniHandler wxPayMiniHandler;

    @Autowired
    private PayProperty payProperty;

    @Bean
    public PayMessageRouter payMessageRouter() {
        final PayMessageRouter wxOpenMessageRouter = new PayMessageRouter();
        //微信jsapi请求
        wxOpenMessageRouter.rule()
                .async(false)
                .payChannel(PayChannelEnum.WX.getType())
                .tradeType(WxPayEnum.JSAPI_MINI.getType())
                .handler(this.wxPayMiniHandler)
                .end();
        wxOpenMessageRouter.rule()
                .async(false)
                .payChannel(PayChannelEnum.WX.getType())
                .tradeType(WxPayEnum.JSAPI_MP.getType())
                .handler(this.wxPayMiniHandler)
                .end();

        //微信h5
        wxOpenMessageRouter.rule()
                .async(false)
                .payChannel(PayChannelEnum.WX.getType())
                .tradeType(WxPayEnum.H5.getType())
                .handler(this.wxPayMiniHandler)
                .end();
        return wxOpenMessageRouter;
    }

    @Bean
    public Snowflake outTradeNoSnowflake() {
        return IdUtil.getSnowflake(payProperty.getWorkerId(), payProperty.getDatacenterId());
    }
}
