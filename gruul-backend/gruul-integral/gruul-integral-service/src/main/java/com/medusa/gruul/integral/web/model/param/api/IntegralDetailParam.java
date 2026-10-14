package com.medusa.gruul.integral.web.model.param.api;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 积分明细
 * @Author: xiaoq
 * @Date: 2023/12/4 9:33
 */
@Data
@Schema(title = "IntegralDetailParam对象", description = "积分明细查询")
public class IntegralDetailParam extends AbstractQueryParam {

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "积分值")
    private BigDecimal integral;

    @Schema(title = "现有积分(计算当前用户的积分值是多少)")
    private BigDecimal nowTheIntegral;


}
