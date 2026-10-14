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
 * 积分明细表
 *
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_detail")
@Schema(title = "IntegralDetail对象", description = "积分明细表 ")
public class IntegralDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "积分明细表id")
    @TableId(value = "integral_detail_id", type = IdType.AUTO)
    private Long integralDetailId;

    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    @Schema(title = "积分订单id")
    @TableField("prize_order_id")
    private Long prizeOrderId;

    @Schema(title = "类别  1. 兑换积分商品(减少) 2.购买商品(增加) 3.使用积分购买商品(减少) 4,登入首次登录,5.每日首次分享 6.签到 7.每日步数达标 8.商家充值9.商家扣除 10.清空")
    @TableField("gain_integral_type")
    private Integer gainIntegralType;

    @Schema(title = "购买商品数量")
    @TableField("quantity")
    private Integer quantity;

    @Schema(title = "积分值")
    @TableField("integral")
    private BigDecimal integral;

    @Schema(title = "现有积分(计算当前用户的积分值是多少)")
    @TableField("now_the_integral")
    private BigDecimal nowTheIntegral;

    @Schema(title = "明细名(预留字段)")
    @TableField("detail_name")
    private String detailName;

    @Schema(title = "是否已经进行清空计算 0-否 1-是")
    @TableField("is_clear")
    private Boolean clear;

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
