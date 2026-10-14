package com.medusa.gruul.datasets.api.entity;

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
 * <p>
 * 订单统计表
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_dc_shop_order_analyse")
@Schema(title = "DcShopOrderAnalyse对象", description = "订单统计表")
public class DcShopOrderAnalyse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "订单统计表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "商铺id")
    @TableField("shop_id")
    private String shopId;

    @Schema(title = "待付款订单")
    @TableField("wait_pay_count")
    private Integer waitPayCount;

    @Schema(title = "待付款订单总金额")
    @TableField("wait_pay_amount")
    private BigDecimal waitPayAmount;

    @Schema(title = "待发货订单")
    @TableField("wait_deliver_count")
    private Integer waitDeliverCount;

    @Schema(title = "待发货总订单金额")
    @TableField("wait_deliver_amount")
    private BigDecimal waitDeliverAmount;

    @Schema(title = "代签收订单")
    @TableField("wait_sign_count")
    private Integer waitSignCount;

    @Schema(title = "代签收订单总金额")
    @TableField("wait_sign_amount")
    private BigDecimal waitSignAmount;

    @Schema(title = "待提货订单")
    @TableField("wait_take_count")
    private Integer waitTakeCount;

    @Schema(title = "待签收订单总金额")
    @TableField("wait_take_amount")
    private BigDecimal waitTakeAmount;

    @Schema(title = "年月日")
    @TableField("yyyyMMdd")
    private Integer yyyyMMdd;


}
