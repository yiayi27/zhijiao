package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.integral.api.entity.IntegralRuleParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 积分参数dto
 * @Author: xiaoq
 * @Date: 2023/12/4 9:15
 */
@Data
@Schema(title = "获取积分规则修改")
public class IntegralRuleParameterDto {
    private Long id;

    @Schema(title = "name")
    private String key;

    @Schema(title = "存储各个规则的值")
    private Integer value;

    @Schema(title = "所属类型 (0.分享  1.登入  2.步数  3.签到)")
    private Integer belongType;

    @Schema(title = "积分规则表id")
    private Long integralRuleId;

    public IntegralRuleParameter coverIntegralRuleParameter() {
        IntegralRuleParameter integralRuleParameter = new IntegralRuleParameter();
        BeanUtil.copyProperties(this, integralRuleParameter);
        return integralRuleParameter;
    }

}
