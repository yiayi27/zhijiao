package com.medusa.gruul.platform.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author whh
 * @description
 * @date: 2023/11/21
 */
@Data
@Accessors(chain = true)
public class LoginDto {

    @Schema(title = "会话密钥")
    private String sessionKey;

    @Schema(title = "用户唯一标识")
    private String openId;

    @Schema(title = "用户在开放平台的唯一标识符")
    private String unionId;

    @Schema(title = "手机号")
    private String mobile;


}
