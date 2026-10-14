package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author whh
 * @description
 * @date: 2023/11/19
 */
@Data
@Schema(title = "用户基本信息")
public class UserInfoVo {

    @Schema(title = "用户店铺id")
    private String shopUserId;

    @Schema(title = "所在国家")
    private String country;

    @Schema(title = "所在省份")
    private String province;

    @Schema(title = "所在城市")
    private String city;

    @Schema(title = "所用的语言 en 英文 - zh_CN 简体中文 -zh_TW 繁体中文 ")
    private String language;

    @Schema(title = "用户名称")
    private String nikeName;

    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "性别 0：未知、1：男、2：女")
    private Integer gender;

    @Schema(title = "首次登陆小程序时间")
    private LocalDateTime firstLoginTime;

    @Schema(title = "是否授权过小程序 false 未授权 turn授权过")
    private Boolean whetherAuthorization;

    @Schema(title = "数据级别为2时才返回,用户扩展信息")
    private UserInfoExtendsVo infoExtends;


    @Schema(title = "数据级别为3时才返回,用户限制类型")
    private UserBlacklistAstrictVo astrictVo;
}
