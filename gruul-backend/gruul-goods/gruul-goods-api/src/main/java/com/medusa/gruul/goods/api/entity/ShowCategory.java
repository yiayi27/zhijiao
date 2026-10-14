package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 展示分类
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_show_category")
@Schema(title = "ShowCategory对象", description = "展示分类")
public class ShowCategory extends BaseEntity {

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
     * 上机分类的编号：0表示一级分类
     */
    @Schema(title = "上机分类的编号：0表示一级分类")
    @TableField("parent_id")
    private Long parentId;

    /**
     * 分类名称
     */
    @NotBlank
    @Size(max = 64)
    @Schema(title = "分类名称")
    @TableField("name")
    private String name;

    /**
     * 分类级别：0->1级；1->2级
     */
    @NotNull
    @Schema(title = "分类级别：0->1级；1->2级")
    @TableField("level")
    private Integer level;

    /**
     * 销售专区
     */
    @Schema(title = "销售专区")
    @TableField("sale_mode")
    private Long saleMode;

    /**
     * 分类排序
     */
    @Schema(title = "分类排序")
    @TableField("sort")
    private Integer sort;

}