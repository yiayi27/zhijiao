package com.medusa.gruul.payment.api.model.pay;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/1/10
 */
@Getter
@Setter
@Accessors(chain = true)
public class PayResult{
    /**
     * 支付服务生成的外部支付订单号
     */
    private String outTradeNo;
    /**
     * 支付数据详情 前端需要的东西
     */
    private Object data;
}
