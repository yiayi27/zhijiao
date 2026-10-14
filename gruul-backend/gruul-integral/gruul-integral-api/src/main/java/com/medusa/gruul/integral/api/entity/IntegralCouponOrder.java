package com.medusa.gruul.integral.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 积分优惠券订单表
 * @Author: xiaoq
 * @Date: 2024/1/9 8:54
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_coupon_order")
@Schema(title = "IntegralCouponOrder对象", description = "积分优惠券订单表")
public class IntegralCouponOrder implements Serializable {

    @Schema(title = "积分优惠券订单id")
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(title = "满足条件金额")
    @TableField("condition_amount")
    private BigDecimal conditionAmount;


    @Schema(title = "优惠金额")
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @Schema(title = "有效类型  0-固定时间 1-领劵立即生效")
    @TableField("effective_type")
    private Integer effectiveType;

    @Schema(title = "有效天数")
    @TableField("effective_days")
    private Integer effectiveDays;

    @Schema(title = "优惠抵扣")
    @TableField("discount")
    private BigDecimal discount;


    @Schema(title = "优惠券类型")
    @TableField("coupon_type")
    private Integer couponType;

    @Schema(title = "实付积分值")
    @TableField("real_pay_integral")
    private BigDecimal realPayIntegral;


    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;


    @Schema(title = "用户微信名")
    @TableField("wxuser_name")
    private String wxuserName;


    @Schema(title = "优惠劵有效期")
    @TableField("coupon_validity")
    private String couponValidity;

    @Schema(title = "发货时间")
    @TableField("send_time")
    private LocalDateTime sendTime;

    @Schema(title = "商家备注")
    @TableField("back_remark")
    private String backRemark;

    @Schema(title = "商铺Id")
    @TableField("shop_id")
    private String shopId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableLogic
    @Schema(title = "删除状态：0->未删除；1->已删除")
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Boolean deleted;

}
