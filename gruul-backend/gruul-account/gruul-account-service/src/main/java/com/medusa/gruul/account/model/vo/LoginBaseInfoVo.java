package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2023/11/19
 */
@Data
@Schema(title = "登录时返回的基本信息")
public class LoginBaseInfoVo {

    @Schema(title = "用户token")
    private String token;

    @Schema(title = "是否授权过小程序 false 未授权  turn授权过,具体授权之后是否取消或者失效,调用微信开放接口查询")
    private Boolean whetherAuthorization;

    @Schema(title = "校验用户当前 session_key 是否有效时需要用到,已经获取用户授权信息解密时需要用到")
    private String sessionKey;

}
