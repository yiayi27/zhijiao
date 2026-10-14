package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 小程序用户会员充值
 *
 * @author zhaokw
 */
@Data
@Accessors(chain = true)
public class MemberPayDto {

    @Schema(title = "支付方式：1-微信支付")
    private Integer paymentType;

    /**
     * 会员卡Id
     */
    @Schema(title = "会员卡Id")
    private Long memberCardInfoId;

    /**
     * 优惠金额
     */
    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;

    /**
     * 充值金额
     */
    @Schema(title = "充值金额")
    private BigDecimal payAmount;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "店铺ID")
    private String shopId;
}
