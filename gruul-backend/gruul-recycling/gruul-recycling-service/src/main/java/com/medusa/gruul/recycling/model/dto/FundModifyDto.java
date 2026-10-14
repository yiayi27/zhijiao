package com.medusa.gruul.recycling.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/4/30
 */
public class FundModifyDto {

    @Schema(title = "主键")
    @NotNull(message = "id为空")
    private Long id;

    @Schema(title = "商品基金是否开启 true开启 false 关闭")
    @NotNull(message = "商品基金不能为空")
    private Boolean goodsFundState;

    @Schema(title = "基金额度")
    @NotNull(message = "基金额度不能为空")
    private BigDecimal fundQuota;

    @Schema(title = "页面背景图片地址")
    @NotBlank(message = "页面背景图片不能为空")
    private String pageBgImg;

    @Schema(title = "购买成功展示背景图地址")
    @NotBlank(message = "购买成功展示背景图不能为空")
    private String buyBgImg;

}
