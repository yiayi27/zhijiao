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
 * 积分商品表
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_goods")
@Schema(title = "IntegralGoods对象", description = "积分商品表")
public class IntegralGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "积分商品表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "积分商城表id")
    @TableField("t_integral_shop_id")
    private Long tIntegralShopId;

    @Schema(title = "积分商品名")
    @TableField("integral_good_name")
    private String integralGoodName;

    @Schema(title = "指导价")
    @TableField("guide_price")
    private BigDecimal guidePrice;

    @Schema(title = "库存")
    @TableField("inventory")
    private Integer inventory;

    @Schema(title = "积分(售卖)")
    @TableField("integral_price")
    private BigDecimal integralPrice;

    @Schema(title = "属性模板id")
    @TableField("attribute_id")
    private Long attributeId;

    @Schema(title = "属性模板名称")
    @TableField("attribute_name")
    private String attributeName;

    @Schema(title = "商品主图")
    @TableField("pic")
    private String pic;

    @Schema(title = "画册图片，连产品图片限制为6张，以逗号分割")
    @TableField("album_pics")
    private String albumPics;

    @Schema(title = "销量(初始销量)")
    @TableField("sales")
    private Integer sales;

    @Schema(title = "运费模板id")
    @TableField("freight_template_id")
    private Long freightTemplateId;

    @Schema(title = "状态(默认上架，0--下架（仓库中），1--上架，2--已售完)")
    @TableField("status")
    private Integer status;

    @Schema(title = "货号")
    @TableField("product_sn")
    private String productSn;

    @Schema(title = "积分商品详情")
    @TableField("detail")
    private String detail;

    @Schema(title = "销售属性")
    @TableField("attribute")
    private String attribute;

    @Schema(title = "单位")
    @TableField("unit")
    private String unit;

    @Schema(title = "商品重量,默认为克")
    @TableField("weight")
    private BigDecimal weight;

    @Schema(title = "预留字段")
    @TableField("type")
    private Boolean type;

    @Schema(title = "商铺Id")
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
