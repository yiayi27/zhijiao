package com.medusa.gruul.sms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 短信模板详情
 * @author lcysike
 */
@Schema(description = "短信模板详情")
@Data
public class SmsTemplateDto {

    @Schema(title = "模版id")
    private Long templateId;

    @Schema(title = "模版名称")
    private String name;

    @Schema(title = "模版内容")
    private String templateContent;

    @Schema(title = "模版类型")
    private Integer type;

    @Schema(title = "模版状态")
    private Integer status;

    @Schema(title = "自定义模版编码")
    private String code;

    @Schema(title = "短信API模板编")
    private String apiTemplateId;

}
