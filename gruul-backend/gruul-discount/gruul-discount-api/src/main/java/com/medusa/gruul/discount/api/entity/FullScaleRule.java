package com.medusa.gruul.discount.api.entity;

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

/**
 * <p>
 * 满减活动规则表
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_full_scale_rule")
@Schema(title = "FullScaleRule对象", description = "满减活动规则表")
public class FullScaleRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 本店店铺id
     */
    @Schema(title = "本店店铺id")
    @TableField("shop_id")
    private String shopId;

    /**
     * 满减活动id
     */
    @Schema(title = "满减活动id")
    @TableField("full_scale_id")
    private Long fullScaleId;

    /**
     * 满减条件：  0-满减劵  1-满折劵
     */
    @Schema(title = "满减条件：  0-满减劵  1-满折劵")
    @TableField("rule_type")
    private Integer ruleType;

    /**
     * 优惠折扣
     */
    @Schema(title = "优惠折扣")
    @TableField("discount")
    private BigDecimal discount;

    /**
     * 优惠金额
     */
    @Schema(title = "优惠金额")
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 满足条件金额
     */
    @Schema(title = "满足条件金额")
    @TableField("condition_amount")
    private BigDecimal conditionAmount;


}
