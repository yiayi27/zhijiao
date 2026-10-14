package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品展示分类
 * </p>
 *
 * @author lcysike
 * @since 2023-10-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_product_show_category")
@Schema(title = "ProductShowCategory对象", description = "商品展示分类")
public class ProductShowCategory extends BaseEntity {

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
     * 上级分类的编号：0表示一级分类
     */
    @Schema(title = "上级分类的编号：0表示一级分类")
    @TableField("parent_id")
    private Long parentId;

    /**
     * 产品id
     */
    @NotNull
    @Schema(title = "产品id")
    @TableField("product_id")
    private Long productId;

    /**
     * 商品展示分类id
     */
    @NotBlank
    @Schema(title = "商品展示分类id")
    @TableField("show_category_id")
    private Long showCategoryId;

}