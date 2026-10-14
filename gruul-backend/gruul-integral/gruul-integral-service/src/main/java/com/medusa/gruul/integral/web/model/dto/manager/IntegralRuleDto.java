package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.integral.api.entity.IntegralRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description: 积分规则dto
 * @Author: xiaoq
 * @Date: 2023/12/4 9:12
 */
@Data
@Schema(title = "积分规则的修改")
public class IntegralRuleDto {

    @Schema(title = "积分规则表id")
    private Long integralRuleId;

    @Schema(title = "积分商城id")
    private Long integralShopId;

    @Schema(title = "积分有效期")
    private Integer integralValidity;

    @Schema(title = "积分使用规则介绍")
    private String useRuleIntroduce;

    @Schema(title = "积分值规则")
    private String integralRuleInfo;

    @Schema(title = "积分值获取类型(0.分享 1.登入 2.签到 3.步数)")
    private Integer ruleType;

    @Schema(title = "是否开启")
    private Boolean open;

    private List<IntegralRuleParameterDto> integralRuleParameterDtos;

    public IntegralRule coverIntegralRule() {
        IntegralRule integralRule = new IntegralRule();
        BeanUtil.copyProperties(this, integralRule);
        return integralRule;
    }
}
