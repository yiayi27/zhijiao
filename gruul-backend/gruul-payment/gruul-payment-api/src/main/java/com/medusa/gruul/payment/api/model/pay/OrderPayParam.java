package com.medusa.gruul.payment.api.model.pay;

import com.medusa.gruul.payment.api.enums.PayType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 张治保
 * date 2024/12/17
 */
@Getter
@Setter
public class OrderPayParam {
    /**
     * 使用的商户配置详情id
     */
    @NotNull
    private PayType payType;
    /**
     * 订单id
     */
    @NotNull
    private Long orderId;

}
