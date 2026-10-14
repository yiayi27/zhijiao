package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description 会员余额明细返回集合
 * @Author zhaokw
 * @Date 00:42 2024\7\4 0007
 **/
@Data
@Schema(title = "会员余额明细返回集合")
@Accessors(chain = true)
public class MemberBalanceRecordVo {

    @Schema(title = "余额明细id")
    private Long id;

    /**
     * 使用余额
     */
    @Schema(title = "使用余额")
    private BigDecimal useBalance;

    /**
     * 剩余余额
     */
    @Schema(title = "剩余余额")
    private BigDecimal remainingBalance;

    /**
     * 会员id
     */
    @Schema(title = "会员id")
    private Long memberInfoId;

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
     * 订单id
     */
    @Schema(title = "订单id")
    private Long orderId;

    /**
     * 交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功
     */
    @Schema(title = "交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功 ")
    private Integer dealType;

    /**
     * 创建时间
     */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;


}
