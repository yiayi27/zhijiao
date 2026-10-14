package com.medusa.gruul.finance.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import com.medusa.gruul.finance.api.enums.BrokerageTypeEnum;
import com.medusa.gruul.finance.api.enums.SettlementStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 佣金明细
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_finance_brokerage_detail")
@Schema(title = "FinanceBrokerageDetail对象", description = "佣金明细")
public class FinanceBrokerageDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(title = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 结算状态：0->未确认；1->已确认；2->已撤销
     */
    @Schema(title = "结算状态：0->未确认；1->已确认；2->已撤销")
    @TableField("status")
    private SettlementStatusEnum status;

    /**
     * 所属用户
     */
    @Schema(title = "所属用户")
    @TableField("user_id")
    private String userId;


    /**
     * 佣金
     */
    @Schema(title = "佣金")
    @TableField("brokerage")
    private BigDecimal brokerage;


    /**
     * 来源订单id
     */
    @Schema(title = "来源订单id")
    @TableField("order_id")
    private Long orderId;

    /**
     * 订单金额
     */
    @Schema(title = "订单金额")
    @TableField("order_amount")
    private BigDecimal orderAmount;


    /**
     * 总销售额
     */
    @Schema(title = "总销售额")
    @TableField("sales_total")
    private BigDecimal salesTotal;


    /**
     * 月份
     */
    @Schema(title = "月份")
    @TableField("month")
    private String month;

    /**
     * 类型：0->普通佣金;1->激励计划
     */
    @Schema(title = "类型：0->普通佣金;1->激励计划")
    @TableField("type")
    private BrokerageTypeEnum type;

    /**
     * 订单流水号
     */
    @Schema(title = "订单流水号")
    @TableField("sn")
    private String sn;

}
