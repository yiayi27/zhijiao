package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * The type Api order evaluate dto.
 * <p>
 * 订单评价
 *
 * @author alan
 * @date 2023/11/25 21:41
 */
@Data
public class ApiOrderEvaluateDto {

    @NotNull
    @Schema(title = "订单ID", example = "1")
    private Long orderId;

    @NotEmpty
    @Schema(title = "订单包含的商品")
    private List<ApiOrderProductEvaluateDto> productEvaluateDtoList;

    @NotNull
    @Schema(title = "商城模式下店铺评分")
    private Integer shopRate;
}
