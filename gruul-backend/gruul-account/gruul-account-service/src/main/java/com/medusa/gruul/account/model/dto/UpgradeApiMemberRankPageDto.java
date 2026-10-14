package com.medusa.gruul.account.model.dto;

import com.medusa.gruul.payment.api.enums.PayType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Description 会员卡支付信息
 * @author zhaokw
 *
 */
@Getter
@Setter
public class UpgradeApiMemberRankPageDto {

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
     * 會員卡號
     */
    @NotNull
    private String memberNumber;

    /**
     * 会员等级代码
     */
    private String rankCode;
}
