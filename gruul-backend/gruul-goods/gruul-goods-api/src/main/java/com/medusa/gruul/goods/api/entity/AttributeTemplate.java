package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 属性模板
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_attribute_template")
@Schema(title = "AttributeTemplate对象", description = "属性模板")
public class AttributeTemplate extends BaseEntity {

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
     * 上级编号：0表示一级
     */
    @Schema(title = "上级编号：0表示一级")
    @TableField("parent_id")
    private Long parentId;

    /**
     * 模板名称
     */
    @NotBlank
    @Size(max = 64)
    @Schema(title = "模板名称")
    @TableField("name")
    private String name;

    /**
     * 内容
     */
    @Schema(title = "内容")
    @TableField("content")
    private String content;

}