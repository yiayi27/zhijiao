package com.medusa.gruul.account.model.dto;

import com.medusa.gruul.payment.api.enums.PayType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 会员到期信息
 *
 * @author zhaokw
 */
@Getter
@Setter
public class UpdateApiMemberExpireTimePageDto {
    /**
     * 支付方式
     */
    @NotNull
    private PayType payType;
    /**
     * 支付金額
     */
    @NotNull
    private BigDecimal payAmount;
    /**
     * 續費天數
     */
    @NotNull
    private Long date;
    /**
     * 會員卡號
     */
    @NotNull
    private String memberNumber;
    /**
     * 会员到期日
     */
    @NotNull
    private String memberExpireTime;
}
