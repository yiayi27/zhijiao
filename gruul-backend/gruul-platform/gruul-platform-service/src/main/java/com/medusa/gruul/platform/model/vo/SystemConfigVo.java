package com.medusa.gruul.platform.model.vo;

import com.medusa.gruul.platform.conf.AliyunStorageConfig;
import com.medusa.gruul.platform.conf.QiniouStorageConfig;
import com.medusa.gruul.platform.conf.SystemConfig;
import com.medusa.gruul.platform.conf.TencentStorageConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 */
@Data
public class SystemConfigVo {

    @Schema(title = "系统配置值")
    private SystemConfig systemConfig;
    @Schema(title = "阿里云配置", hidden = true)
    private AliyunStorageConfig aliyunStorageConfig;
    @Schema(title = "七牛云配置", hidden = true)
    private QiniouStorageConfig qiniouStorageConfig;
    @Schema(title = "腾讯云配置", hidden = true)
    private TencentStorageConfig tencentStorageConfig;
}
