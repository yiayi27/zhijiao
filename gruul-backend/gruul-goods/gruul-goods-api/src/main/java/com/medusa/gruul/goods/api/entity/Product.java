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

import java.math.BigDecimal;

/**
 * <p>
 * 商品信息
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_product")
@Schema(title = "Product对象", description = "商品信息")
public class Product extends BaseEntity {

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
     * 供应商id
     */
    @Schema(title = "供应商id")
    @TableField("provider_id")
    private Long providerId;

    /**
     * 配送方式(0--商家配送，1--快递配送 2--同城配送)
     */
    @NotNull
    @Schema(title = "配送方式(0--商家配送，1--快递配送 2--同城配送)")
    @TableField("distribution_mode")
    private Integer distributionMode;

    /**
     * 运费模板ID
     */
    @Schema(title = "运费模板ID")
    @TableField("freight_template_id")
    private Long freightTemplateId;


    /**
     * 佣金类型
     */
    @Schema(title = "佣金类型(0--金额，1--比例)")
    @TableField("commission_type")
    private Integer commissionType;


    /**
     * 属性模版ID
     */
    @NotNull
    @Schema(title = "属性模版ID")
    @TableField("attribute_id")
    private Long attributeId;

    /**
     * 属性模板名称
     */
    @NotNull
    @Schema(title = "属性模板名称")
    @TableField("attribute_name")
    private String attributeName;

    /**
     * 限购类型(默认统一规格，0--统一规格，1--统一限购，2--规格限购)
     */
    @NotNull
    @Schema(title = "限购类型(默认统一规格，0--统一规格，1--统一限购，2--规格限购)")
    @TableField("limit_type")
    private Integer limitType;

    /**
     * 销售专区
     */
    @NotNull
    @Schema(title = "销售专区(默认商超系统，0--商超系统，2--限时秒杀)")
    @TableField("sale_mode")
    private Long saleMode;

    /**
     * 商品名称
     */
    @NotBlank
    @Schema(title = "商品名称")
    @TableField("name")
    private String name;

    /**
     * 展示图片
     */
    @NotBlank
    @Schema(title = "展示图片")
    @TableField("pic")
    private String pic;

    /**
     * 宽屏展示图片
     */
    @Schema(title = "宽屏展示图片")
    @TableField("wide_pic")
    private String widePic;

    /**
     * 画册图片，连产品图片限制为6张，以逗号分割
     */
    @NotBlank
    @Schema(title = "画册图片，连产品图片限制为6张，以逗号分割")
    @TableField("album_pics")
    private String albumPics;

    /**
     * 视频url
     */
    @NotBlank
    @Schema(title = "视频url")
    @TableField("video_url")
    private String videoUrl;

    /**
     * 货号
     */
    @Schema(title = "货号")
    @TableField("product_sn")
    private String productSn;

    /**
     * 状态(默认上架，0--下架，1--上架)
     */
    @Schema(title = "状态(默认上架，0--下架，1--上架)")
    @TableField("status")
    private Integer status;

    /**
     * 接龙状态(0--未参加，1--已参加)
     */
    @Schema(title = "接龙状态(0--未参加，1--已参加)")
    @TableField("domino_state")
    private Integer dominoState;

    /**
     * 排序
     */
    @Schema(title = "排序")
    @TableField("sort")
    private Integer sort;

    /**
     * 销量
     */
    @Schema(title = "销量")
    @TableField("sale")
    private Integer sale;

    /**
     * 赠送的积分
     */
    @Schema(title = "赠送的积分")
    @TableField("gift_point")
    private Integer giftPoint;

    /**
     * 单位
     */
    @Schema(title = "单位")
    @TableField("unit")
    private String unit;

    /**
     * 商品重量，默认为克
     */
    @NotNull
    @Schema(title = "商品重量，默认为克")
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮
     */
    @Schema(title = "以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮")
    @TableField("service_ids")
    private String serviceIds;


    /**
     * 商品详情
     */
    @Schema(title = "商品详情")
    @TableField("detail")
    private String detail;

    /**
     * 规格是否展开
     */
    @Schema(title = "规格是否展开")
    @TableField("is_open_specs")
    private Boolean openSpecs;

    /**
     * 卖点描述
     */
    @Schema(title = "卖点描述")
    @TableField("sale_describe")
    private String saleDescribe;

    /**
     * 销售属性
     */
    @Schema(title = "销售属性")
    @TableField("attribute")
    private String attribute;

    /**
     * 评分
     */
    @Schema(title = "评分")
    @TableField("score")
    private BigDecimal score;

    /**
     * 是否使用会员价(0--不使用, 1--固定金额, 2--固定百分比)
     */
    @Schema(title = "是否使用会员价(0--不使用, 1--固定金额, 2--固定百分比)")
    @TableField("use_member_price")
    private Integer useMemberPrice;

    /**
     * 是否使用返利(0--不使用, 1--固定金额, 2--固定百分比)
     */
    @Schema(title = "是否使用返利(0--不使用, 1--固定金额, 2--固定百分比)")
    @TableField("use_rebate")
    private Integer useRebate;

    /**
     * 是否使用分销(0--不使用, 1--固定金额, 2--固定百分比)
     */
    @Schema(title = "是否使用分销(0--不使用, 1--固定金额, 2--固定百分比)")
    @TableField("use_distribution")
    private Integer useDistribution;

    /**
     * 分销佣金参数
     */
    @Schema(title = "分销佣金参数")
    @TableField("distribution_amount")
    private BigDecimal distributionAmount;

}