package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type Api order product evaluate dto.
 * <p>
 * 订单商品评价
 *
 * @author alan
 * @date 2023/11/25 21:59
 */
@Data
public class ApiOrderProductEvaluateDto {

    @NotNull
    @Schema(title = "商品评分")
    private Integer rate;
    @NotNull
    @Schema(title = "商品ID", example = "1")
    private Long productSkuId;
    @NotBlank
    @Schema(title = "商品评价")
    private String comment;
    @NotBlank
    @Schema(title = "商品评价图片")
    private String picture;
}
