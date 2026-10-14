package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 后台设置会员请求参数
 * @Author: zhaokw29701
 * @Date: 2024/5/5 23:58
 **/
@Data
@Accessors(chain = true)
public class SetMemberDto {

    @Schema(title = "用户id")
    @NotBlank(message = "参数为空")
    private String userId;
    @Schema(title = "会员等级代码")
    @NotBlank(message = "参数为空")
    private String rankCode;
    @Schema(title = "店铺id")
    private String shopId;
    @Schema(title = "手机号")
    @NotBlank(message = "参数为空")
    private String phone;
    @Schema(title = "验证码")
    @NotBlank(message = "参数为空")
    private String code;
}
