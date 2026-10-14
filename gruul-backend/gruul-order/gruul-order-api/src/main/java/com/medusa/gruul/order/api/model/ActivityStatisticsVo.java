package com.medusa.gruul.order.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author alan
 */
@Data
public class ActivityStatisticsVo {
    /**
     * 参与人数
     */
    @Schema(title = "参与人数")
    private Integer peopleNum;


    /**
     * 支付订单数
     */
    @Schema(title = "支付订单数")
    private Integer payOrder;


    /**
     * 金额总数
     */
    @Schema(title = "金额总数")
    private BigDecimal gaapTotal;


    /**
     * 活动ID
     */
    @Schema(title = "活动ID")
    private Long activityId;
}
