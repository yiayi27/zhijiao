package com.medusa.gruul.platform.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/7/24
 */
@Data
public class PublicShopInfoVo {

    @Schema(title = "小程序的appId")
    private String miniAppId;

    @Schema(title = "小程序原始id")
    private String userName;
}
