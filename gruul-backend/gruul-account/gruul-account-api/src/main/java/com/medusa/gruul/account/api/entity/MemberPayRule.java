package com.medusa.gruul.account.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 会员卡储值信息表
 * </p>
 *
 * @author zhaokw
 * @since 2024-06-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_pay_rule")
@Schema(title = "MemberPayRule对象", description = "会员卡储值信息表")
public class MemberPayRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员卡注册状态 0无优惠,1有优惠
     */
    @Schema(title = "会员卡注册状态 0无优惠,1有优惠")
    @TableField("state")
    private Integer state;

    /**
     * 优惠金额
     */
    @Schema(title = "优惠金额")
    @TableField("discount_amount")
    @DecimalMin(value = "0.01", message = "优惠金额不能低于0.01")
    @DecimalMax(value = "100000000", message = "优惠金额不能大于1个亿")
    private BigDecimal discountAmount;

    /**
     * 充值金额
     */
    @Schema(title = "充值金额")
    @TableField("pay_amount")
    @DecimalMin(value = "0.01", message = "充值金额不能低于0.01")
    @DecimalMax(value = "100000000", message = "充值金额不能大于1个亿")
    private BigDecimal payAmount;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

}
