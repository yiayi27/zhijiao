package com.medusa.gruul.finance.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Trade overview vo.
 *
 * @description: 对账单概览
 * @author: alan
 * @date: 2023/11/5 21:29
 */
@Data
@Schema(title = "对账单概览")
public class TradeOverviewVo implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 收入次数
     */
    @Schema(title = "收入次数")
    private Integer incomeNumber;


    /**
     * 收入金额
     */
    @Schema(title = "收入金额")
    private BigDecimal incomeAmount;


    /**
     * 支出次数
     */
    @Schema(title = "支出次数")
    private Integer expensesNumber;


    /**
     * 支出金额
     */
    @Schema(title = "支出金额")
    private BigDecimal expensesAmount;
}
