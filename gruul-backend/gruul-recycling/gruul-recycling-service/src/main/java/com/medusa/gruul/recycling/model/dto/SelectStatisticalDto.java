package com.medusa.gruul.recycling.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @data: 2024/5/5
 */
@Data
public class SelectStatisticalDto {
    @Schema(title = "总基金数")
    private String totalFundQuota;
    @Schema(title = "交易总数")
    private String totalOrderNumber;
}
