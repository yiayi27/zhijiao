package com.medusa.gruul.integral.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 积分商品属性表
 * </p>
 *
 * @author alan
 * @since 2023-11-20
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_goods_attribute")
@Schema(title = "IntegralGoodsAttribute对象", description = "积分商品属性表")
public class IntegralGoodsAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "积分商品属性表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @Schema(title = "积分商品表id")
    @TableField("integral_goods_id")
    private Long integralGoodsId;

    @Schema(title = "属性名称")
    @TableField("name")
    private String name;

    @Schema(title = "属性值")
    @TableField("value")
    private String value;

    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    @TableField(fill = FieldFill.INSERT)

    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)


    private LocalDateTime updateTime;
    @TableLogic
    @Schema(title = "删除状态：0->未删除；1->已删除")
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Boolean deleted;

}
