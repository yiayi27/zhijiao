package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 控制台创建店铺
 *
 * @author whh
 * @description
 * @date: 2024/3/8
 */
@Data
public class ShopInfoConsoleCreateDto {

    @Schema(title = "店铺名称")
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    @Schema(title = "店铺首页图片")
    @NotBlank(message = "店铺首页图片不能为空")
    private String logoUrl;
    
}
