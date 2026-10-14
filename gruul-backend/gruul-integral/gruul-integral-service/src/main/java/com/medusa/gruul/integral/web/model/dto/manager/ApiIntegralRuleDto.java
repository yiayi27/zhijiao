package com.medusa.gruul.integral.web.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 积分规则dto
 * @Author: xiaoq
 * @Date: 2023/12/21 16:32
 */
@Data
@Schema(title = "API端积分规则Dto")
public class ApiIntegralRuleDto {
    @Schema(title = "积分规则表id")
    private Long integralRuleId;

    @Schema(title = "积分值获取类型(0.分享 1.登入 2.签到 3.步数)")
    private Integer ruleType;

    @Schema(title = "是否开启")
    private Boolean open;

}
