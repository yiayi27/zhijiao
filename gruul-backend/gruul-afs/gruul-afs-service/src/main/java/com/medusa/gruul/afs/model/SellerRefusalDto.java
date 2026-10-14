package com.medusa.gruul.afs.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type Seller refusal dto.
 *
 * @author alan
 * @description: SellerRefusalDto.java
 * @date 2024/8/5 22:47
 */
@Data
@Schema(title = "商家拒绝的参数")
public class SellerRefusalDto {

    @NotNull
    @Schema(title = "售后订单id")
    private Long afsId;

    @NotBlank
    @Schema(title = "商家拒绝原因")
    private String refusalReason;
}
