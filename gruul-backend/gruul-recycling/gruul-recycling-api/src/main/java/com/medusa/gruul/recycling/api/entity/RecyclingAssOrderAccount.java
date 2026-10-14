package com.medusa.gruul.recycling.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 基金所得订单用户关联表
 * </p>
 *
 * @author whh
 * @since 2024-05-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_recycling_ass_order_account")
@Schema(title = "RecyclingAssOrderAccount对象", description = "基金所得订单用户关联表")
public class RecyclingAssOrderAccount extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(title = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 店铺用户id
     */
    @Schema(title = "店铺用户id")
    @TableField("shop_user_id")
    private Long shopUserId;

    /**
     * 获得基金额度
     */
    @Schema(title = "交易总额")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 订单数
     */
    @Schema(title = "订单数")
    @TableField("order_number")
    private Integer orderNumber;

    /**
     * 最后交易时间
     */
    @Schema(title = "最后交易时间")
    @TableField("last_pay_time")
    private LocalDateTime lastPayTime;

    @Schema(title = "总基金数")
    @TableField("total_fund_quota")
    private BigDecimal totalFundQuota;


    /**
     * 用户名称
     */
    @Schema(title = "用户名称")
    @TableField("nick_name")
    private String nickName;

    /**
     * 头像url
     */
    @Schema(title = "头像url")
    @TableField("avatar_url")
    private String avatarUrl;

}
