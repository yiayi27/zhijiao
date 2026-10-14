package com.medusa.gruul.discount.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 满减活动规则
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "FullScaleRuleVo", description = "满减活动规则查询返回信息")
public class FullScaleRuleVo {

    private Long id;

    @Schema(title = "活动id")
    private Long fullScaleId;

    @Schema(title = "满减条件：  0-满减劵  1-满折劵")
    private Integer ruleType;

    @Schema(title = "优惠折扣")
    private BigDecimal discount;

    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(title = "满足条件金额")
    private BigDecimal conditionAmount;

}
