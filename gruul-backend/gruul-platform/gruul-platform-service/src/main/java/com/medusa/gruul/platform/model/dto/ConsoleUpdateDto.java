package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/3/9
 */
@Data
public class ConsoleUpdateDto {

    @Schema(title = "店铺名称")
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    @Schema(title = "营业时间,自行分割")
    @NotBlank(message = "经营时间错误")
    private String businessHours;

    @Schema(title = "店铺电话")
    private String shopPhone;

    @Schema(title = "小程序底部打标")
    private String miniBottomLog;

    @Schema(title = "店铺首页图片")
    private String logoUrl;

    @NotBlank
    @Schema(title = "appid", description = "appid（小程序或其它）主要用于微信支付，和小程序登录")
    private String appid;

    @NotBlank
    @Schema(title = "appSecret", description = "小程序密钥 用于小程序登录")
    private String appSecret;
}
