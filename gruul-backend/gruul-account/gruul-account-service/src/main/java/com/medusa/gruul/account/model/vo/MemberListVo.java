package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 会员信息列表
 * @Author: zhaokw29701
 * @Date: 2024/5/1 21:23
 **/
@Data
@Schema(title = "会员信息列表")
@Accessors(chain = true)
public class MemberListVo {

    @Schema(title = "会员用户余额")
    private BigDecimal supplyBonus;

    @Schema(title = "会员卡返利余额")
    private BigDecimal rebateBonus;

    @Schema(title = "店铺用户id")
    private String userId;

    @Schema(title = "用户名称")
    private String nikeName;

    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "最后交易时间")
    private LocalDateTime orderLastDealTime;

    @Schema(title = "消费次数")
    private Integer consumeNum;

    @Schema(title = "交易总额")
    private BigDecimal consumeTotleMoney;

    @Schema(title = "可用积分")
    private BigDecimal integral;

    @Schema(title = "用户拥护的标签")
    private List<UserTagVo> userTagVos;

    @Schema(title = "首次登陆小程序时间")
    private LocalDateTime firstLoginTime;

    @Schema(title = "会员信息列表")
    private List<MemberInnerListVo> memberInnerListVos;

    @Schema(title = "第一次成为会员时间")
    private LocalDateTime memberRegisterTime;

}
