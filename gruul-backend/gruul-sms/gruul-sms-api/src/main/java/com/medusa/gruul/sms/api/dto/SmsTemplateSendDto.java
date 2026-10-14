package com.medusa.gruul.sms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 短信模板的发送信息
 * @author lcysike
 */
@Schema(description = "短信模板的发送信息")
@Data
public class SmsTemplateSendDto {

    @Schema(title = "用户id")
    private String userId;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @NotNull(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    @NotNull(message = "模板编码不能为空")
    private Long templateCode;

    @Schema(description = "模板参数")
    private String templateParams;

}
