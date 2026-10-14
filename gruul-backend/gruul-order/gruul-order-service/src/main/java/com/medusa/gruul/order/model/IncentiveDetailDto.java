package com.medusa.gruul.order.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * The type Incentive detail dto.
 * <p>
 * 查询激励佣金详情
 *
 * @author alan
 * @date 2023/11/10 9:21
 */
@Data
@Schema(title = "查询激励佣金详情")
public class IncentiveDetailDto extends AbstractQueryParam {


    @NotBlank
    @Schema(title = "查询的月份")
    private String month;

    @NotNull
    @Schema(title = "账户类型枚举")
    private String accountType;

    @NotBlank
    @Schema(title = "对应的账户ID")
    private String userId;

    @Schema(title = "配送方式 0->全部;100->社区订单;102->快递订单")
    private Integer deliverType;
}
