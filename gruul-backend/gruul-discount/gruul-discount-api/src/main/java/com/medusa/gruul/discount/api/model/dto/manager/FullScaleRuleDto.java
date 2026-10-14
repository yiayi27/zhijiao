package com.medusa.gruul.discount.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.discount.api.entity.FullScaleRule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 满减活动规则信息
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "新增或修改满减活动规则信息DTO")
public class FullScaleRuleDto {

    private Long id;

    @Schema(title = "满减活动id")
    private Long fullScaleId;

    @Schema(title = "满减条件：  0-满减劵  1-满折劵")
    @NotBlank(message = "满减条件不能为空")
    private Integer ruleType;

    @Schema(title = "优惠折扣")
    private BigDecimal discount;

    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(title = "满足条件金额")
    @NotBlank(message = "满足条件金额不能为空")
    private BigDecimal conditionAmount;

    public FullScaleRule coverFullScaleRule() {
        FullScaleRule fullScaleRule = new FullScaleRule();
        BeanUtil.copyProperties(this, fullScaleRule);
        return fullScaleRule;
    }
}
