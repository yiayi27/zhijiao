package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lcysike
 * @description
 * @data: 2024/11/10
 */
@Data
@Schema(title = "用户分销信息")
public class UserDistributionVo {

    @Schema(title = "当前累积佣金收益")
    private BigDecimal totalDistributionAmount;

    @Schema(title = "分销客户数量")
    private Integer distributionUserCount;

}
