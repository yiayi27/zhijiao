package com.medusa.gruul.payment.api.model.pay;

import com.medusa.gruul.payment.api.enums.FeeTypeEnum;
import com.medusa.gruul.payment.api.enums.PayType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author 张治保
 * date 2024/12/24
 */
@Getter
@Setter
@Accessors(chain = true)
public class PayParam {

    /**
     * 内部订单id
     */
    @NotNull
    private String orderId;
    /**
     * 订单支付类型
     */
    @NotNull
    private PayType payType;
    /**
     * 订单名称
     */
    @NotNull
    private String subject;
    /**
     * 订单描述
     */
    @NotNull
    private String body;
    /**
     * rabbitmq路由key 支付成功回调
     */
    @NotNull
    private String routeKey;
    /**
     * 支付货币类型 默认 人民币
     */
    @NotNull
    private FeeTypeEnum feeType = FeeTypeEnum.CNY;
    /**
     * 支付价格
     */
    @NotNull
    private BigDecimal price;
    /**
     * 订单支付超时时间 单位秒
     */
    @NotNull
    @Min(value = 1)
    private Long seconds;
    /**
     * 附加json数据
     */
    private String attach;


}
