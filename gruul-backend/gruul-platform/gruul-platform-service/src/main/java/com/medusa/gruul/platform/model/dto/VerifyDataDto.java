package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/12/21
 */
@Data
public class VerifyDataDto {
    @Schema(title = "手机号")
    @NotBlank
    private String phone;
    
    @NotNull(message = "校验类型错误")
    @Schema(title = "1.校验给定手机号是否当前用户手机号")
    private Integer option;
}
