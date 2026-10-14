package com.medusa.gruul.finance.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.finance.api.enums.FlowTypeEnum;
import com.medusa.gruul.finance.api.enums.TradeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 资金交易明细表
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_finance_trade_detail")
@Schema(title = "FinanceTradeDetail对象", description = "资金交易明细表")
public class FinanceTradeDetail extends BaseEntity {

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
     * 交易编号
     */
    @Schema(title = "交易编号")
    @TableField("trade_no")
    private String tradeNo;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 用户帐号
     */
    @Schema(title = "用户帐号")
    @TableField("user_name")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    @TableField("user_avatar_url")
    private String userAvatarUrl;

    /**
     * 交易类型
     */
    @Schema(title = "交易类型:0->商品交易;1->退款; 6->用户提现;7->用户返利")
    @TableField("type")
    private TradeTypeEnum type;

    /**
     * 金额
     */
    @Schema(title = "金额")
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 来源id
     */
    @Schema(title = "来源id")
    @TableField("source_id")
    private Long sourceId;

    /**
     * 资金流向:0->收入;1->支出
     */
    @Schema(title = "资金流向:0->收入;1->支出")
    @TableField("capital_flows")
    private FlowTypeEnum capitalFlows;

    /**
     * 订单流水号
     */
    @Schema(title = "订单流水号")
    @TableField("sn")
    private String sn;

}
