package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 商品会员价格表
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_price")
@Schema(title = "MemberPrice对象", description = "商品会员价格表")
public class MemberPrice extends BaseEntity {

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
     * 产品id
     */
    @NotNull
    @Schema(title = "产品id")
    @TableField("product_id")
    private Long productId;

    /**
     * 产品sku-id
     */
    @NotNull
    @Schema(title = "产品sku-id")
    @TableField("sku_id")
    private Long skuId;

    /**
     * 会员等级id
     */
    @NotNull
    @Schema(title = "会员等级id")
    @TableField("member_level_id")
    private Long memberLevelId;

    /**
     * 会员价格/比例
     */
    @NotNull
    @Schema(title = "会员价格/比例")
    @TableField("member_price")
    private BigDecimal memberPrice;

    /**
     * 会员等级名称
     */
    @Schema(title = "会员等级名称")
    @TableField("member_level_name")
    private String memberLevelName;

}