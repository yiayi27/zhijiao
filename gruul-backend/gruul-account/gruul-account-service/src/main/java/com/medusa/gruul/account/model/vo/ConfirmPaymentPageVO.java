package com.medusa.gruul.account.model.vo;

import com.medusa.gruul.payment.api.enums.PayType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Description 会员卡支付信息
 * @author zhaokw
 *
 */
@Data
public class ConfirmPaymentPageVO {

    /**
     * 支付方式
     */
    @NotNull
    private PayType payType;
    /**
     * 支付金额
     */
    @NotNull
    private BigDecimal payAmount;
    /**
     * 会员卡id
     */
    @NotNull
    private Long memberCardInfoId;

}
