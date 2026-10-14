package com.medusa.gruul.account.api.entity;

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
 * 会员储值订单表
 * </p>
 *
 * @author zhaokw
 * @since 2024-06-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_order_info")
@Schema(title = "MemberOrderInfo", description = "会员储值订单表")
public class MemberOrderInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    @Schema(title = "订单id")
    @TableField("order_id")
    private Long orderId;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 会员卡id
     */
    @Schema(title = "会员卡id")
    @TableField("member_card_id")
    private Long memberCardId;

    /**
     * 充值金额
     */
    @Schema(title = "充值金额")
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 付款时间
     */
    @Schema(title = "付款时间")
    @TableField("pay_time")
    private LocalDateTime payTime;

    /**
     * 可用余额（表示充值之后当时可用余额）
     */
    @Schema(title = "可用余额（表示充值之后当时可用余额）")
    @TableField("usable_amount")
    private BigDecimal usableAmount;

    /**
     * 备注
     */
    @Schema(title = "备注")
    @TableField("note")
    private String note;

    /**
     * 是否覆盖 0:不覆盖；1：覆盖
     */
    @Schema(title = "是否覆盖 0:不覆盖；1：覆盖")
    @TableField("state")
    private Integer state;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

}
