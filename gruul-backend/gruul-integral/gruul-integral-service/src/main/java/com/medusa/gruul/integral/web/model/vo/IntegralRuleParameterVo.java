package com.medusa.gruul.integral.web.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 积分规则参数vo
 * @Author: xiaoq
 * @Date: 2023/11/28 14:42
 */
@Data
@Schema(title = "IntegralRuleParameterVo对象", description = "积分规则参数信息")
public class IntegralRuleParameterVo {

    private Long id;

    @Schema(title = "name")
    private String key;

    @Schema(title = "存储各个规则的值")
    private Integer value;

    @Schema(title = "所属类型 (0.分享  1.登入  2.步数  3.签到)")
    private Integer belongType;

}
