package com.medusa.gruul.recycling.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/4/30
 */
public class CouponModifyDto {
    @Schema(title = "主键ID")
    @NotNull(message = "id为空")
    private Long id;

    @Schema(title = "回收券功能是否开启  true开启 false 关闭")
    @NotNull(message = "回收券功能不能为空")
    private Boolean recyclingState;

    @Schema(title = "商品详情回收入口是否开启  true开启 false 关闭")
    @NotNull(message = "商品详情回收入口不能为空")
    private Boolean entranceState;

    @Schema(title = "按钮样式配置")
    @NotNull(message = "按钮样式配置不能为空")
    private @Valid List<BtnStyleDto> btnStyles;

    @Schema(title = "关联卡券id")
    private Long assCouponId;

    @Schema(title = "页面轮播图,传入图片地址url，用，号分割")
    private String pageCarousels;

    @Schema(title = "活动说明")
    private String activityDesc;

    @Schema(title = "提交按钮图片")
    private String submitImg;
}
