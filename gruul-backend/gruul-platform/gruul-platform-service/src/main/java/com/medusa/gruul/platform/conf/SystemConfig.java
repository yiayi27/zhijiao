package com.medusa.gruul.platform.conf;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/3/29
 */
@Data
public class SystemConfig {

    @Schema(title = "小程序请求域名")
    private String miniDomain;

    @Schema(title = "控制台地址")
    private String consoleUrl;

    @Schema(title = "官网地址")
    private String consoleLog;

    @Schema(title = "代理后台地址")
    private String agentUrl;

}
