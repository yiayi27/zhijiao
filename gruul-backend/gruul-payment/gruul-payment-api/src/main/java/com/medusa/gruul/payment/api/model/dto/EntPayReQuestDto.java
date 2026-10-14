package com.medusa.gruul.payment.api.model.dto;

import com.medusa.gruul.payment.api.enums.CheckNameEnum;
import com.medusa.gruul.payment.api.enums.PayChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author create by zq
 * @date created in 2023/11/18
 */
@Data
public class EntPayReQuestDto {

    /**
     * 主键id
     */
    @Schema(title = "主键id")
    private Long id;


    /**
     * 支付渠道:1-微信
     */
    @Schema(title = "支付渠道:1-微信")
    private PayChannelEnum payChannel;


    /**
     * 订单总金额，单位为分
     */
    @Schema(title = "订单总金额，单位为分")
    private Integer amount;


    /**
     * 企业付款备注
     */
    @Schema(title = "企业付款备注")
    private String description;


    /**
     * 附加数据,格式为json字符串,怎么发送怎么返回
     */
    @Schema(title = "附加数据,格式为json字符串,怎么发送怎么返回")
    private String businessParams;


    /**
     * 调用接口的机器Ip地址
     */
    @Schema(title = "调用接口的机器Ip地址")
    private String spbillCreateIp;


    /**
     * 支付流水号
     */
    @Schema(title = "支付流水号")
    private String transactionId;


    /**
     * 交易状态：1、 发起支付 2、 交易支付成功 3、交易支付失败 4、交易同步返回成功单业务未成功
     */
    @Schema(title = "交易状态：1、 发起支付 2、 交易支付成功 3、交易支付失败 4、交易同步返回成功单业务未成功")
    private Integer tradeStatus;

    /**
     * 用户标识
     */
    @Schema(title = "用户标识")
    private String openid;


    /**
     * 校验用户姓名选项
     */
    @Schema(title = "校验用户姓名选项")
    private CheckNameEnum checkName;


    /**
     * 收款用户姓名. 可选
     */
    @Schema(title = "收款用户姓名.")
    private String reUserName;


    /**
     * 订单id
     */
    @Schema(title = "订单id.")
    private String orderId;


    /**
     * 订单金额
     */
    @Schema(title = "订单金额")
    private Integer totalFee;

}
