package com.medusa.gruul.order.api.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建换货单的参数
 *
 * @author alan
 * @date 2024/8/23 13:02
 */
@Data
@Schema(title = "创建换货单的参数")
public class ExchangeOrderDto {
    /**
     * 售后申请单ID
     */
    @Schema(title = "售后申请单ID")
    @TableField("afs_id")
    private Long afsId;

    /**
     * 原始订单
     */
    @Schema(title = "原始订单")
    @TableField("order_id")
    private Long orderId;

    /**
     * 商品sku编号
     */
    @Schema(title = "商品sku编号")
    @TableField("product_sku_id")
    private Long productSkuId;

    /**
     * 商品数量
     */
    @Schema(title = "商品数量")
    @TableField("product_quantity")
    private Integer productQuantity;

    /**
     * 是否提货点申请
     */
    @Schema(title = "是否提货点申请")
    @TableField("point_apply")
    private Boolean pointApply;
}
