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
public class PopupWindowDto {
    @Schema(title = "主键ID")
    @NotNull(message = "id为空")
    private Long id;


    @Schema(title = "首页未寄出提示背景图地址")
    @NotBlank(message = "首页未寄出提示背景图不能为空")
    private String homePromptBgImg;

}
