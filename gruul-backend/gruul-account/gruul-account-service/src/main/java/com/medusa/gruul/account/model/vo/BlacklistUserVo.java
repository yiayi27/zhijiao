package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2023/11/20
 */
@Data
public class BlacklistUserVo {

    @Schema(title = "会员卡编号")
    private String memberNumber;

    @Schema(title = "会员卡余额")
    private BigDecimal supplyBonus;

    @Schema(title = "会员注册时间")
    private LocalDateTime memberRegisterTime;

    @Schema(title = "成为会员天数")
    private Integer becomeMemberDayNumber;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "用户名称")
    private String nikeName;

    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "消费次数")
    private Integer consumeNum;

    @Schema(title = "交易总额")
    private BigDecimal consumeTotleMoney;

    @Schema(title = "可用积分")
    private BigDecimal integral;

    @Schema(title = "当前限制  1-限制下单,2-限制评论")
    private List<Integer> blacklistType;

}
