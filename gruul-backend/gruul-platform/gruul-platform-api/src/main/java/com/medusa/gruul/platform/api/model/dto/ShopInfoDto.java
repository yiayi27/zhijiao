package com.medusa.gruul.platform.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author whh
 * @description
 * @date: 2024/7/24
 */
@Data
@Accessors(chain = true)
public class ShopInfoDto {

    @Schema(title = "店铺首页图片")
    private String logoUrl;

    @Schema(title = "店铺名称")
    private String shopName;
}
