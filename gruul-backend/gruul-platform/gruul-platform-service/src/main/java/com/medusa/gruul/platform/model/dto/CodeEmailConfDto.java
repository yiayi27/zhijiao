package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2024/11/28
 */
@Data
public class CodeEmailConfDto {
    @Schema(title = "阿里云AccessKeyId")
    private String accessKeyId;
    @Schema(title = "阿里云AccessKeySecret")
    private String accessKeySecret;
    @Schema(title = "模板id")
    private String templateCode;
    @Schema(title = "接收手机号")
    private List<String> codePhones;


    @Schema(title = "通知接收邮箱")
    private List<String> emails;

    @Schema(title = "邮件通知email")
    private List<EmailCode> emailNotify;

    @Schema(title = "电子发票email")
    private List<EmailCode> electronicInvoiceEmail;

    @Data
    public static class EmailCode {
        @Schema(title = "发件人邮箱")
        private String email;
        @Schema(title = "授权码")
        private String authCode;
        @Schema(title = "smtp服务器")
        private String host;
        @Schema(title = "smtp服务器端口")
        private String port;
    }
}
