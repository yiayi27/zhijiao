package com.medusa.gruul.account.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author xiaoq
 * @date 2024/2/22 13:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_mini_account_collect")
@Schema(title = "MiniAccountCollect对象", description = "用户收藏表")
public class MiniAccountCollect extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "collect_id", type = IdType.AUTO)
    private Long collectId;

    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    @Schema(title = "商品id")
    @TableField("product_id")
    private Long productId;


    @Schema(title = "商品主图")
    @TableField("product_pic")
    private String productPic;

    @Schema(title = "商品名称")
    @TableField("product_name")
    private String productName;

    @Schema(title = "商品状态  0-上架 1-下架 2-售罄")
    @TableField("status")
    private Integer status;

    @Schema(title = "商品实际售价")
    @TableField("product_price")
    private BigDecimal productPrice;

    @Schema(title = "指导价划线价")
    @TableField("original_price")
    private BigDecimal originalPrice;


    @Schema(title = "备注")
    @TableField("ramark")
    private String ramark;


    @Schema(title = "商铺Id")
    @TableField("shop_id")
    private String shopId;


}
