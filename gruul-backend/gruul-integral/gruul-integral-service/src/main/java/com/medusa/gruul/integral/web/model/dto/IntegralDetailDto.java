package com.medusa.gruul.integral.web.model.dto;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 商家查看用户积分明细
 * @Author: xiaoq
 * @Date: 2024/1/14 13:13
 */
@Data
@Schema(title = "IntegralDetailDto对象", description = "积分明细条件查询")
public class IntegralDetailDto extends AbstractQueryParam {

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "开始日期----‘2024-01-02’")
    private String startTime;


    @Schema(title = "结束日期----‘2024-01-05’")
    private String endTime;

}
