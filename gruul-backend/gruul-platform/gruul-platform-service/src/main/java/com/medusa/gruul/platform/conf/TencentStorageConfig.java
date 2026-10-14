package com.medusa.gruul.platform.conf;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * @author whh
 */
@Data
public class TencentStorageConfig {

    @NotBlank(message = "腾讯云绑定的域名不能为空")
    @URL(message = "腾讯云绑定的域名格式不正确")
    @Schema(title = "腾讯云绑定的域名")
    private String qcloudDomain;

    @Schema(title = "腾讯云路径前缀")
    private String qcloudPrefix;

    @NotNull(message = "腾讯云AppId不能为空")
    @Schema(title = "腾讯云AppId")
    private Integer qcloudAppId;

    @NotBlank(message = "腾讯云SecretId不能为空")
    @Schema(title = "腾讯云SecretId")
    private String qcloudSecretId;

    @Schema(title = "腾讯云AppId")
    @NotBlank(message = "腾讯云SecretKey")
    private String qcloudSecretKey;

    @Schema(title = "腾讯云AppId")
    @NotBlank(message = "腾讯云BucketName")
    private String qcloudBucketName;

    @Schema(title = "腾讯云AppId")
    @NotBlank(message = "腾讯云COS所属地区")
    private String qcloudRegion;
}
