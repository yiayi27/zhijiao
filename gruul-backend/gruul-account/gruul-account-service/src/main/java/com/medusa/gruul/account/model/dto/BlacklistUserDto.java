package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author whh
 * @description
 * @date: 2023/12/3
 */
@Data
public class BlacklistUserDto {

    @Schema(title = "会员卡编号")
    private String memberNumber;

    @Schema(title = "会员卡余额")
    private BigDecimal supplyBonus;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "店铺用户id")
    private String shopUserId;

    @Schema(title = "用户名称")
    private String nikeName;

    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "会员注册时间")
    private LocalDateTime memberRegisterTime;

    @Schema(title = "首次登陆小程序时间")
    private LocalDateTime firstLoginTime;

    @Schema(title = "消费次数")
    private Integer consumeNum;

    @Schema(title = "交易总额")
    private BigDecimal consumeTotleMoney;

    @Schema(title = "可用积分")
    private BigDecimal integral;
}
