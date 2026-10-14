package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.integral.api.entity.StepNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 用户步数相关DTO
 * @Author: xiaoq
 * @Date: 2023/12/16 16:31
 */
@Data
@Schema(title = "用户兑换步數所需相关信息DTO")
public class UserStepNumberDto {

    @Schema(title = "用户当前步数")
    private Integer todayStep;

    @Schema(title = "目标步数步数")
    private Integer targetStep;

    @Schema(title = "可兑换积分")
    private BigDecimal integral;

    @Schema(title = "完成度 --百分比")
    private String completionDegree;

    public StepNumber coverStepNumber() {
        StepNumber stepNumber = new StepNumber();
        BeanUtil.copyProperties(this, stepNumber);
        return stepNumber;
    }
}
