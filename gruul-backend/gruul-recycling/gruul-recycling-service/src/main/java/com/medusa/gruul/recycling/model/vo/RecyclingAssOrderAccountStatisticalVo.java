package com.medusa.gruul.recycling.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/5/5
 */
public class RecyclingAssOrderAccountStatisticalVo {
    @Schema(title = "总基金数")
    private String totalFundQuota;
    @Schema(title = "交易总数")
    private String totalOrderNumber;
    @Schema(title = "用户排名-小程序端使用")
    private String ranking;
}
