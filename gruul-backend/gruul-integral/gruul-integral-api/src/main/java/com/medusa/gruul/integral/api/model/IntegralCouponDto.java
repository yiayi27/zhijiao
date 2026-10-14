package com.medusa.gruul.integral.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 兑换积分优惠券Dto
 * @Author: whh
 * @Date: 2024/1/7 9:56
 */
@Data
public class IntegralCouponDto {

    @NotNull
    @Schema(title = "所需积分值")
    private BigDecimal integralValue;

    @NotNull
    @Schema(title = "优惠券有效期")
    private String couponValidity;


    @Schema(title = "微信昵称")
    private String wxuserName;

    @Schema(title = "收货人姓名")
    private String receiverName;

    @NotNull
    @Schema(title = "用户Id")
    private String userId;

    @NotNull
    @Schema(title = "优惠券类型")
    private Integer couponType;

    @Schema(title = "优惠折扣（不能大于10）")
    private BigDecimal discount;

    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(title = "满足条件金额")
    private BigDecimal conditionAmount;

    @Schema(title = "优惠券天数")
    private Integer effectiveDays;

    @Schema(title = "有效期类型 0-固定时间 1-领劵立即生效")
    private Integer effectiveType;


}
