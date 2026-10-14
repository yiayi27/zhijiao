package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 产品评论表
 * </p>
 *
 * @author alan
 * @since 2023-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_product_evaluate")
@Schema(title = "OrderProductEvaluate对象", description = "产品评论表")
public class OrderProductEvaluate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 订单id
     */
    @Schema(title = "订单id")
    @TableField("order_id")
    private Long orderId;

    /**
     * 商品评分
     */
    @Schema(title = "商品评分")
    @TableField("rate")
    private Integer rate;

    /**
     * 商品id
     */
    @Schema(title = "商品id")
    @TableField("product_id")
    private Long productId;


    /**
     * 商品图片
     */
    @Schema(title = "商品图片")
    @TableField("product_pic")
    private String productPic;

    /**
     * 商品名
     */
    @Schema(title = "商品名")
    @TableField("product_name")
    private String productName;


    /**
     * 销售价格
     */
    @Schema(title = "销售价格")
    @TableField("product_price")
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    @Schema(title = "购买数量")
    @TableField("product_quantity")
    private Integer productQuantity;


    /**
     * 商品sku编号
     */
    @Schema(title = "商品sku编号")
    @TableField("product_sku_id")
    private Long productSkuId;

    /**
     * 商品的销售属性
     */
    @Schema(title = "商品的销售属性")
    @TableField("specs")
    private String specs;

    /**
     * 评论内容
     */
    @Schema(title = "评论内容")
    @TableField("comment")
    private String comment;

    /**
     * 上传的图片
     */
    @Schema(title = "上传的图片")
    @TableField("picture")
    private String picture;

    /**
     * 是否精选
     */
    @Schema(title = "是否精选")
    @TableField("choice")
    private Boolean choice;

    /**
     * 卖家回复
     */
    @Schema(title = "卖家回复")
    @TableField("reply")
    private String reply;


}
