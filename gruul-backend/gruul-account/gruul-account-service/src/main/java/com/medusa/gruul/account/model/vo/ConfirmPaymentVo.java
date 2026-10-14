package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Description 小程序端购买弹框点击确认付款或确认兑换请求参数
 * @Author zhaokw
 * @Date 23:58 2024\6\4 0004
 **/
@Data
@Schema(title = "小程序端购买弹框点击确认付款或确认兑换请求参数")
@Accessors(chain = true)
public class ConfirmPaymentVo {

    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换 ")
    private Integer openType;

    @Schema(title = "支付方式：1-微信支付 2-余额支付")
    private Integer paymentType;

    @Schema(title = "openType为1时，表示扣除金额；openType为2时，表示当前用户扣除积分")
    private BigDecimal availableAmount;

    @Schema(title = "openType为1时，表示当前用户可用余额；openType为2时，表示当前用户可用积分")
    private BigDecimal useAmount;

    @Schema(title = "会员卡id")
    private Long memberCardInfoId;

    /**
     * 使用期限 按天计算 0表示不限制
     */
    @Schema(title = "使用期限 按天计算  0表示不限制")
    private Long useInDate;

    /**
     * 会员卡编号
     */
    @Schema(title = "会员卡编号")
    private String memberNumber;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "店铺ID")
    private String shopId;

    @Schema(title = "开通会员的订阅消息ID")
    private String memberTemplateId;


}
