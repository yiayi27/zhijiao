package com.medusa.gruul.recycling.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/4/30
 */
public class BtnStyleDto {

    @Schema(title = "菜单名称")
    @NotBlank(message = "菜单名称，不能为空")
    private String btnName;
    @Schema(title = "背景颜色")
    @NotBlank(message = "背景颜色，不能为空")
    private String color;
    @Schema(title = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    public BtnStyleDto() {
    }

    public BtnStyleDto(@NotBlank(message = "菜单名称，不能为空") String btnName, @NotBlank(message = "背景颜色，不能为空") String color, @NotNull(message = "排序不能为空") Integer sort) {
        this.btnName = btnName;
        this.color = color;
        this.sort = sort;
    }
}
