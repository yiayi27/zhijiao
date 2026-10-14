package com.medusa.gruul.logistics.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 聚合统计
 *
 * @author lcy
 */
@Data
@Schema(title = "物流聚合统计")
public class LogisticsAggregationDto {

    @Schema(title = "模版id")
    private Long freightTemplateId;

    @Schema(title = "总金额")
    private BigDecimal priceSum;

    @Schema(title = "总件数")
    private Integer numSum;

    @Schema(title = "总重量")
    private BigDecimal weightSum;


}
