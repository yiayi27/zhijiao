package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Description 支付回调公共类
 * @Author zhaokw
 * @Date 22:17 2024\7\15 0015
 **/
@Data
@Schema(title = "支付回调公共类")
@Accessors(chain = true)
public class MemberCompletedVo {

    @Schema(title = "会员卡id")
    private Long id;


    /**
     * 会员卡编号
     */
    @Schema(title = "会员卡编号")
    private String mNumber;

    /**
     * 用户id/会员等级
     */
    @Schema(title = "用户id/会员等级")
    private String userRank;

    /**
     * 优惠金额
     */
    @Schema(title = "优惠金额")
    private BigDecimal dAmount;

    /**
     * 充值金额
     */
    @Schema(title = "充值金额")
    private BigDecimal pAmount;

    /**
     * 种类
     */
    @Schema(title = "种类：1-开通会员 2-续费会员 3-升级会员 4-会员充值 5-其他 6-用户充值")
    private Integer type;

    @Schema(title = "续费天数")
    private Long date;


}
