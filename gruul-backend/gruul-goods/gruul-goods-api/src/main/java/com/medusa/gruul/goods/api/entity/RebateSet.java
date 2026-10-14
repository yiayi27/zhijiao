package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 店铺返利设置表
 * </p>
 *
 * @author lcysike
 * @since 2024-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_rebate_set")
@Schema(title = "RebateSet对象", description = "店铺返利设置表")
public class RebateSet extends BaseEntity {

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
     * 商品返利比例
     */
    @Schema(title = "商品返利比例")
    @TableField("rebate_ratio")
    private BigDecimal rebateRatio;

    /**
     * 余额使用条件(0--普通用户，1--会员),多个以逗号分割
     */
    @Schema(title = "余额使用条件(0--普通用户，1--会员),多个以逗号分割")
    @TableField("balance_use_condition")
    private String balanceUseCondition;

    /**
     * 返利条件(0--普通用户，1--会员),多个以逗号分割
     */
    @Schema(title = "返利条件(0--普通用户，1--会员),多个以逗号分割")
    @TableField("rebate_condition")
    private String rebateCondition;

}