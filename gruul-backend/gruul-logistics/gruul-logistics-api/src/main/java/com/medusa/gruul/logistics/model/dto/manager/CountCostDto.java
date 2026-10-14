package com.medusa.gruul.logistics.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CountCostDto {
    /**
     * 解释
     */
    @Schema(title = "解释")
    private String des;
    /**
     * 运费金额 单位 :元
     */
    @Schema(title = "运费金额 单位 :元")
    private BigDecimal cost;

}
