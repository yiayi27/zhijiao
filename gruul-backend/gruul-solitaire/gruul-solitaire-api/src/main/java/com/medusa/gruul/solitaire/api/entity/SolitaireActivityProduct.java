package com.medusa.gruul.solitaire.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 接龙活动商品表
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_solitaire_activity_product")
@Schema(title = "SolitaireActivityProduct对象", description = "接龙活动商品表")
public class SolitaireActivityProduct extends BaseEntity {

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
     * 接龙活动id
     */
    @Schema(title = "接龙活动id")
    @TableField("activity_id")
    private Long activityId;

    /**
     * 商品id
     */
    @Schema(title = "商品id")
    @TableField("product_id")
    private Long productId;
}
