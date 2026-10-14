package com.medusa.gruul.order.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 每日交易详情
 *
 * @author alan
 * @date 2023/11/5 21:29
 */
@Data
@Schema(title = "每日交易详情")
public class TradeLineVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(title = "交易量")
    private Integer transactionVolume = 0;

    @Schema(title = "交易额")
    private BigDecimal turnover = BigDecimal.ZERO;

    @Schema(title = "日期")
    private String date;
}
