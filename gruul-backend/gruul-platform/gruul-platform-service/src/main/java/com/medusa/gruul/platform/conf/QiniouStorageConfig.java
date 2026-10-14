package com.medusa.gruul.platform.conf;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * @author whh
 */
@Data
public class QiniouStorageConfig {

    @NotBlank(message = "七牛绑定的域名不能为空")
    @URL(message = "七牛绑定的域名格式不正确")
    @Schema(title = "七牛绑定的域名")
    private String qiniuDomain;

    @Schema(title = "七牛路径前缀")
    private String qiniuPrefix;

    @NotBlank(message = "七牛AccessKey不能为空")
    @Schema(title = "七牛ACCESS_KEY")
    private String qiniuAccessKey;

    @Schema(title = "七牛SECRET_KEY")
    @NotBlank(message = "七牛SecretKey不能为空")
    private String qiniuSecretKey;

    @NotBlank(message = "七牛空间名不能为空")
    @Schema(title = "七牛存储空间名")
    private String qiniuBucketName;
}
