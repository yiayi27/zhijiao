package com.medusa.gruul.logistics.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 运费计算请求参数
 *
 * @author 王鹏
 */
@Data
@NoArgsConstructor
@Schema(title = "运费计算")
public class LogisticsFreightDto {


    @NotNull
    @Schema(title = "运费模版id")
    private Long freightTemplateId;

    @NotNull
    @Schema(title = "件数")
    private Integer num;

    @NotNull
    @Schema(title = "每件重量")
    private BigDecimal weight;

    @NotNull
    @Schema(title = "区域编码")
    private String region;


    @NotNull
    @Schema(title = "金额")
    private BigDecimal price;


}
