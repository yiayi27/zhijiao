package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type Order share setting vo.
 * @author alan
 */
@Schema(title = "设置分享晒单设置")
@Data
public class OrderShareSettingVo {
    /**
     * 标题
     */
    @NotBlank
    @Schema(title = "标题")
    private String title;

    /**
     * 背景图
     */
    @NotBlank
    @Schema(title = "背景图")
    private String background;

    /**
     * 是否重置为默认值
     */
    @NotNull
    @Schema(title = "是否重置为默认值")
    private boolean defaultValue;

}
