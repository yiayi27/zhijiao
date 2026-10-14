package com.medusa.gruul.platform.model.dto;

import com.medusa.gruul.platform.conf.AliyunStorageConfig;
import com.medusa.gruul.platform.conf.QiniouStorageConfig;
import com.medusa.gruul.platform.conf.SystemConfig;
import com.medusa.gruul.platform.conf.TencentStorageConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;

/**
 * 云存储配置信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-25 16:12
 */
@Data
public class SaveConfigDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Range(min = 0, max = 3, message = "类型错误")
    @Schema(title = "保存类型 0-域名配置 1：七牛  2：阿里云  3：腾讯云")
    private Integer type;

    @Schema(title = "域名配置")
    private SystemConfig systemConfig;
    @Schema(title = "阿里云配置")
    private AliyunStorageConfig aliyunStorageConfig;
    @Schema(title = "七牛云配置")
    private QiniouStorageConfig qiniouStorageConfig;
    @Schema(title = "腾讯云配置")
    private TencentStorageConfig tencentStorageConfig;

}
