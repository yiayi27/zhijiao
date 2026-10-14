package com.medusa.gruul.account.model.dto;

import com.medusa.gruul.payment.api.enums.PayType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author create by zq
 * @description 充值参数
 */
@Getter
@Setter
public class RechargeDto {
    /**
     * pay type
     */
    @NotNull(groups = {Member.class, User.class})
    private PayType payType;
    /**
     * 规则id
     */
    @NotNull(groups = {Member.class})
    private Long ruleId;
    /**
     * 充值金额
     */
    @NotNull(groups = {User.class})
    private BigDecimal payAmount;

    /**
     * 会员充值
     */
    public interface Member {
    }

    /**
     * 普通用户充值
     */
    public interface User {
    }


}

