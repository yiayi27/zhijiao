package com.medusa.gruul.integral.web.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description: 积分规则vo 基础信息
 * @Author: xiaoq
 * @Date: 2023/11/28 14:39
 */
@Data
@Schema(title = "IntegralRuleVo对象", description = "积分规则信息")
public class IntegralRuleVo {

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

    private List<IntegralRuleParameterVo> integralRuleParameterVos;

}
