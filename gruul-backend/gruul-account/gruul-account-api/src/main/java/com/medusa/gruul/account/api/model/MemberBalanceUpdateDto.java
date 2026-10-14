package com.medusa.gruul.account.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Description 扣除余额与余额明细
 * @Author zhaokw
 * @Date 22:34 2024\7\8 0008
 **/
@Data
@Accessors(chain = true)
public class MemberBalanceUpdateDto {

    /**
     * 使用余额
     */
    @Schema(title = "使用余额")
    private BigDecimal useBalance;

    /**
     * 会员id
     */
    @Schema(title = "会员id")
    private Long memberInfoId;

    /**
     * shopUserId
     */
    @Schema(title = "shopUserId")
    private String shopUserId;

    /**
     * 变化类型 0-添加 1-减少
     */
    @Schema(title = "变化类型 0-添加 1-减少")
    private Integer changeType;

    /**
     * 明细名
     */
    @Schema(title = "明细名")
    private String detailName;

    /**
     * 交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功 4-商品分销 5-提现
     */
    @Schema(title = "交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功 4-商品分销 5-提现")
    private Integer dealType;

    /**
     * 订单来源：1-会员，2-商品，3-积分
     */
    @Schema(title = "订单来源：1-会员，2-商品，3-积分")
    private Integer source;

    /**
     * 外部订单id
     */
    @Schema(title = "外部订单id")
    private Long outOrderId;

}
