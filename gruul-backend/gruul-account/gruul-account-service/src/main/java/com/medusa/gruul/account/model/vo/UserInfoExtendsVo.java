package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2023/11/19
 */
@Data
@Schema(title = "用户扩展信息")
public class UserInfoExtendsVo {

    @Schema(title = "会员卡")
    List<ApiMemberCardInfoVo> memberInfo;
    @Schema(title = "拥有积分值")
    private BigDecimal integral;
    @Schema(title = "拥有总积分值")
    private BigDecimal integralTotal;
    @Schema(title = "连续签到天数")
    private Integer clockNum;
    @Schema(title = "最后交易时间")
    private LocalDateTime lastDealTime;
    @Schema(title = "是否黑名单用户 0-否 1-是")
    private Integer isBlacklist;
    @Schema(title = "消费次数")
    private Integer consumeNum;
    @Schema(title = "用户最后一次选择的地理位置经纬度 经纬度,经度在前维度在后逗号分隔")
    private String lastChooseLcation;
    @Schema(title = "商铺id")
    private String shopId;
    @Schema(title = "最后登录时间")
    private LocalDateTime lastLoginTime;
    @Schema(title = "店铺用户id")
    private String shopUserId;
    /**
     * 用户余额
     */
    @Schema(title = "用户余额")
    private BigDecimal supplyBonus;

    /**
     * 用户返利余额
     */
    @Schema(title = "用户返利余额")
    private BigDecimal rebateBonus;

    /**
     * 冻结余额
     */
    @Schema(title = "冻结余额")
    private BigDecimal freezeBonus;
}
