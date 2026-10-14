package com.medusa.gruul.platform.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/4/3
 */
@Data
public class MiniAuthInfoDto {

    /**
     * 小程序accessToken
     */
    @Schema(title = "小程序accessToken")
    private String accessToken;


    @Schema(title = "小程序appId")
    private String appId;
}
