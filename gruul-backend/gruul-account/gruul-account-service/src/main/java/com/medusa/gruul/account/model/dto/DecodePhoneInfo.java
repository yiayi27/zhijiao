package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 手机号码解密信息
 * @author whh
 * @description
 * @date: 2023/11/26
 */
@Data
public class DecodePhoneInfo {

    @Schema(title = "加密数据")
    @NotBlank(message = "参数为空")
    private String encryptedData;
    @Schema(title = "偏移量")
    @NotBlank(message = "参数为空")
    private String ivStr;
    @Schema(title = "登录时返回的sessionKey")
    @NotBlank(message = "参数为空")
    private String sessionKey;
}
