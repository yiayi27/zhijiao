package com.medusa.gruul.integral.web.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Description: 微信运动数据解密
 * @Author: xiaoq
 * @Date: 2023/12/16 9:48
 */
@Data
public class DecryptRunData {

    @Schema(title = "加密数据")
    @NotBlank(message = "参数为空")
    private String encryptedData;
    @Schema(title = "偏移量")
    @NotBlank(message = "参数为空")
    private String ivStr;
    @Schema(title = "登录时返回的sessionKey")
    @NotBlank(message = "参数为空")
    private String sessionKey;

}
