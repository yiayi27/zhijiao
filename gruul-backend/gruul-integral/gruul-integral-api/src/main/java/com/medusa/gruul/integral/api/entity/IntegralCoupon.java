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
 * <p>
 * 积分优惠券表
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_coupon")
@Schema(title = "IntegralCoupon对象", description = "积分优惠券表")
public class IntegralCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "优惠券id")
    @TableId(value = "coupon_id", type = IdType.AUTO)
    private Long couponId;

    @Schema(title = "积分商城表id")
    @TableField("t_integral_shop_id")
    private Long tIntegralShopId;

    @Schema(title = "优惠券名称")
    @TableField("coupon_name")
    private String couponName;

    @Schema(title = "优惠卷类型（1无门槛折扣卷  2折扣卷 3满减卷  4无门槛现金卷）")
    @TableField("coupon_type")
    private Integer couponType;

    @Schema(title = "折扣 (无门槛折扣劵   无门槛现金劵)|(满减劵 满折劵)")
    @TableField("discount")
    private Integer discount;

    @Schema(title = "(满折劵|满减劵)  满多少")
    @TableField("meet")
    private Integer meet;

    @Schema(title = "发行量")
    @TableField("circulation")
    private Integer circulation;

    @Schema(title = "限制领取数")
    @TableField("limit_get")
    private Integer limitGet;

    @Schema(title = "所需积分")
    @TableField("integral_price")
    private BigDecimal integralPrice;

    @Schema(title = "预留字段")
    @TableField("is_effective")
    private Boolean effective;

    @Schema(title = "优惠券生效时间")
    @TableField("effective_starttime")
    private LocalDateTime effectiveStarttime;

    @Schema(title = "优惠券失效时间")
    @TableField("effective_endtime")
    private LocalDateTime effectiveEndtime;

    @Schema(title = "状态(默认上架，0--下架（仓库中），1--上架，2--已售完)")
    @TableField("status")
    private Integer status;

    @Schema(title = "预留字段")
    @TableField("type")
    private Boolean type;

    @Schema(title = "店铺ID")
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
