package com.medusa.gruul.integral.web.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 积分优惠券订单DTO
 * @Author: xiaoq
 * @Date: 2024/1/7 15:39
 */
@Data
@Schema(title = "积分优惠券订单DTO")
public class IntegralCouponOrderDto {

    @Schema(title = "积分优惠券订单id")
    private Long id;

    @Schema(title = "满足条件金额")
    private BigDecimal conditionAmount;

    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(title = "优惠抵扣")
    private BigDecimal discount;

    @Schema(title = "优惠券类型")
    private Integer couponType;

    @Schema(title = "优惠券天数")
    private Integer effectiveDays;

    @Schema(title = "有效期类型 0-固定时间 1-领劵立即生效")
    private Integer effectiveType;

    @Schema(title = "用户微信名")
    private String wxuserName;

    @Schema(title = "实付积分值")
    private BigDecimal realPayIntegral;

    @Schema(title = "优惠劵有效期")
    private String couponValidity;

    @Schema(title = "发货时间")
    private LocalDateTime sendTime;

    @Schema(title = "商家备注")
    private String backRemark;

}
