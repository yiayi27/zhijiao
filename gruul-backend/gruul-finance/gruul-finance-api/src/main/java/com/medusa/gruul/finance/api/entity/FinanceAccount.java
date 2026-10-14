package com.medusa.gruul.finance.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 资金账户
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_finance_account")
@Schema(title = "FinanceAccount对象", description = "资金账户")
public class FinanceAccount extends BaseEntity {

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
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 累计佣金
     */
    @Schema(title = "累计佣金")
    @TableField("brokerage_total")
    private BigDecimal brokerageTotal;

    /**
     * 当前佣金（可提现）
     */
    @Schema(title = "当前佣金（可提现）")
    @TableField("brokerage")
    private BigDecimal brokerage;

    /**
     * 预计佣金
     */
    @Schema(title = "预计佣金")
    @TableField("brokerage_frozen")
    private BigDecimal brokerageFrozen;

    /**
     * 源ID
     */
    @Schema(title = "源ID")
    @TableField("source_id")
    private String sourceId;


}
