package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 商品规格结构表
 *
 * @author 张治保
 * 2021年11月12日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_product_spec")
@Schema(title = "商品规格结构类")
public class ProductSpec implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    @Schema(title = "规格id")
    private Long id;
    /**
     * 本店店铺id
     */
    @Schema(hidden = true)
    private String shopId;
    /**
     * 所属商品id
     */
    @Schema(hidden = true)
    private Long productId;
    /**
     * 规格名(parentId为0时为规格组名)
     */
    @TableField(value = "`name`")
    @NotNull
    @Schema(title = "规格名称")
    private String name;
    /**
     * 父规格ID(规格组的parentId为0) 默认为0
     */
    @Schema(title = "父规格Id")
    private Long parentId;
    /**
     * 排序
     */
    @TableField(value = "`order`")
    @Schema(title = "排序")
    private int order;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @TableField(
            fill = FieldFill.INSERT_UPDATE
    )
    @Schema(title = "更新时间")
    private LocalDateTime updateTime;
    /**
     * 删除状态：0->未删除；1->已删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @Schema(hidden = true)
    private Boolean deleted;
    /**
     * 版本号
     */
    @Version
    @Schema(title = "版本号")
    private Integer version;
    /**
     * 新增更新时 辅助字段
     */
    @TableField(exist = false)
    private List<ProductSpec> children;
}