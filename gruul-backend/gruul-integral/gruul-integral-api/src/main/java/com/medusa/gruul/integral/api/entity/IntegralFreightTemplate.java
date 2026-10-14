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
 * 积分商品运费模版
 * </p>
 *
 * @author alan
 * @since 2023-11-20
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_freight_template")
@Schema(title = "IntegralFreightTemplate对象", description = "积分商品运费模版")
public class IntegralFreightTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "积分商品运费模板id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @Schema(title = "模板名称")
    @TableField("name")
    private String name;

    @Schema(title = "积分商品表id")
    @TableField("integral_goods_id")
    private Long integralGoodsId;

    @Schema(title = "计费类型:0->按重量；1->按件数")
    @TableField("charge_type")
    private Integer chargeType;

    @Schema(title = "首重kg")
    @TableField("first_weight")
    private BigDecimal firstWeight;

    @Schema(title = "首费（元）")
    @TableField("first_fee")
    private BigDecimal firstFee;

    @Schema(title = "续重")
    @TableField("continue_weight")
    private BigDecimal continueWeight;

    @Schema(title = "续费")
    @TableField("continue_fee")
    private BigDecimal continueFee;

    @Schema(title = "目的地（省、市）")
    @TableField("dest")
    private String dest;

    @Schema(title = "店铺ID")
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
