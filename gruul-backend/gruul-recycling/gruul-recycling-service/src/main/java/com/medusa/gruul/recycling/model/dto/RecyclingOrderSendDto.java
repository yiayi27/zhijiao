package com.medusa.gruul.recycling.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/5/7
 */
public class RecyclingOrderSendDto {


    @Schema(title = "主键")
    @NotNull(message = "订单id不能为空")
    private Long id;


    @Schema(title = "物流公司")
    @NotBlank(message = "物流公司为空")
    private String shipmentName;

    @Schema(title = "物流单号")
    @NotBlank(message = "物流单号为空")
    private String shipmentNumber;

    /**
     * 凭证图片
     */
    @Schema(title = "凭证图片")
    private String proofImgs;


}
