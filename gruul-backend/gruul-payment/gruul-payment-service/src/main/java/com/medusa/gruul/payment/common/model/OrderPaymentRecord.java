package com.medusa.gruul.payment.common.model;

import com.medusa.gruul.payment.api.enums.PayType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 订单支付记录
 * @author 张治保
 * date 2022/1/20
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class OrderPaymentRecord {
    /**
     * 支付回调参数
     */
    private String notifyParam;
    /**
     * 支付类型
     */
    private PayType payType;
    /**
     * 商户配置详情id
     */
    private String detailsId;

}
