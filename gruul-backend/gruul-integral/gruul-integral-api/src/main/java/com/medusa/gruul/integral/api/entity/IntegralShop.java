package com.medusa.gruul.integral.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 积分商城表
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_shop")
@Schema(title = "IntegralShop对象", description = "步数表")
public class IntegralShop implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "积分商城表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "类型 :0积分商城关闭 1.积分商城开启")
    @TableField("type")
    private Boolean type;

    @Schema(title = "积分抵扣比例百分比")
    @TableField("ratio")
    private BigDecimal ratio;

    @Schema(title = "顶底抵扣上限额度百分比")
    @TableField("ceiling")
    private BigDecimal ceiling;

    @Schema(title = "店铺ID")
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
