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

/**
 * <p>
 * 满减活动关联商品表
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_full_scale_product")
@Schema(title = "FullScaleProduct对象", description = "满减活动关联商品表")
public class FullScaleProduct extends BaseEntity {

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
     * 产品id
     */
    @Schema(title = "产品id")
    @TableField("product_id")
    private Long productId;


}
