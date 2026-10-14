package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/3/23
 */
@Data
public class PasswordRetrieveDto {

    @Schema(title = "商户手机号")
    @NotBlank(message = "商户手机号不能为空")
    private String phone;

    @Schema(title = "校验码凭证")
    @NotBlank(message = "商户手机号不能为空")
    private String certificate;

    @Schema(title = "新密码")
    private String passwd;

}
