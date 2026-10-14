package com.medusa.gruul.order.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type Query user order dto.
 * <p>
 * 查询用户订单列表参数
 *
 * @author alan
 * @date 2023/11/13 20:54
 */
@Data
@Schema(title = "查询用户订单列表参数", description = "查询用户订单列表参数")
public class QueryUserOrderDto extends AbstractQueryParam {


    @NotNull
    @Schema(title = "要查询的订单状态：101->待发货;102->配送中;103->待提货;")
    private Integer status;

    @NotNull
    @Schema(title = "要查询的客户ID", example = "1")
    private String userId;
}