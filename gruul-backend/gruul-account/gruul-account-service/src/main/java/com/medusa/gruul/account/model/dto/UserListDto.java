package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author whh
 * @description
 * @date: 2023/11/28
 */
@Data
public class UserListDto {

    @Schema(title = "会员卡编号")
    private String memberNumber;

    @Schema(title = "会员卡余额")
    private BigDecimal supplyBonus;

    @Schema(title = "会员卡返利余额")
    private BigDecimal rebateBonus;

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

    @Schema(title = "最后交易时间")
    private LocalDateTime orderLastDealTime;

    @Schema(title = "首次登陆小程序时间")
    private LocalDateTime firstLoginTime;

    @Schema(title = "消费次数")
    private Integer consumeNum;

    @Schema(title = "交易总额")
    private BigDecimal consumeTotleMoney;

    @Schema(title = "可用积分")
    private BigDecimal integral;

    @Schema(title = "分销订单数")
    private Integer orderCount;

    @Schema(title = "一级分销员id")
    private String firstUserId;

    @Schema(title = "所属分销员名称")
    private String firstNickName;

    @Schema(title = "所属分销员手机")
    private String firstPhone;

    @Schema(title = "佣金")
    private BigDecimal firstDistributionAmount;


}
