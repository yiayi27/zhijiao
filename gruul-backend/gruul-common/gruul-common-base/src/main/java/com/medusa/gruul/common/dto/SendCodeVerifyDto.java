package com.medusa.gruul.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 */
@Data
public class SendCodeVerifyDto {

    @Schema(title = "验证码")
    private String code;

    @Schema(title = "手机号")
    private String phone;

    @Schema(title = "校验类型")
    private Integer type;

    public SendCodeVerifyDto() {
    }

    public SendCodeVerifyDto(String code, String phone, Integer type) {
        this.code = code;
        this.phone = phone;
        this.type = type;
    }
}
