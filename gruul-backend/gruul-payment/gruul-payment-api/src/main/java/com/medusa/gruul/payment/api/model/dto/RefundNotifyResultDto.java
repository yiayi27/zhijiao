package com.medusa.gruul.payment.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * @author create by zq
 * @date created in 2024/03/13
 */
@Data
public class RefundNotifyResultDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 返回状态码.
     */
    @Schema(title = "返回状态码")
    protected String returnCode;
    /**
     * 返回信息.
     */
    @Schema(title = "返回信息")
    protected String returnMsg;
    @Schema(title = "业务订单号")
    private String outTradeNo;
    @Schema(title = "附加数据,格式为json字符串,怎么发送怎么返回")
    private String businessParams;
    private String shopId;
    @Schema(title = "支付流水号")
    private String transactionId;
    /**
     * 商户退款单号.
     */
    @Schema(title = "商户退款单号")
    private String outRefundNo;
    /**
     * 微信退款单号.
     */
    @Schema(title = "微信退款单号")
    private String refundId;
    /**
     * 退款金额.
     */
    @Schema(title = "退款金额")
    private Integer refundFee;
    /**
     * 应结退款金额.
     */
    @Schema(title = "应结退款金额")
    private Integer settlementRefundFee;
    /**
     * 业务结果.
     */
    @Schema(title = "业务结果")
    private String resultCode;

    /**
     * 错误代码.
     */
    @Schema(title = "错误代码")
    private String errCode;

    /**
     * 错误代码描述.
     */
    @Schema(title = "错误代码描述")
    private String errCodeDes;

}
