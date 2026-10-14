package com.medusa.gruul.payment.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author whh
 * @since 2023-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_payment")
@Schema(title = "Payment对象", description = "")
public class Payment extends BaseEntity {

    /**
     * 主键id
     */
    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 该笔订单允许的最晚付款时间，逾期将关闭交易。
     */
    @Schema(title = "该笔订单允许的最晚付款时间，逾期将关闭交易。")
    @TableField("timeout_express")
    private LocalDateTime timeoutExpress;

    /**
     * 支付渠道:1-微信
     */
    @Schema(title = "支付渠道:1-微信 2-支付宝")
    @TableField("pay_channel")
    private Integer payChannel;

    /**
     * 货币类型 CNY：人民币,符合ISO 4217标准的三位字母代码
     */
    @Schema(title = "货币类型 CNY：人民币,符合ISO 4217标准的三位字母代码(枚举)")
    @TableField("fee_type")
    private String feeType;

    /**
     * 订单总金额，单位为元，精确到小数点后两位
     */
    @Schema(title = "订单总金额，单位为元，精确到小数点后两位")
    @TableField("total_fee")
    private BigDecimal totalFee;

    /**
     * 对一笔交易的具体描述信息
     */
    @Schema(title = "对一笔交易的具体描述信息")
    @TableField("body")
    private String body;

    /**
     * 附加数据,格式为json字符串,怎么发送怎么返回
     */
    @Schema(title = "附加数据,格式为json字符串,怎么发送怎么返回")
    @TableField("business_params")
    private String businessParams;

    /**
     * 终端ip
     */
    @Schema(title = "终端ip")
    @TableField("terminal_ip")
    private String terminalIp;

    /**
     * 商户订单号
     */
    @Schema(title = "支付流水号")
    @TableField("transaction_id")
    private String transactionId;

    /**
     * 业务回调地址
     */
    @Schema(title = "业务回调地址")
    @TableField("business_notify_url")
    private String businessNotifyUrl;

    /**
     * 路由键
     */
    @Schema(title = "路由键,路由键和回调url必须选一个")
    @TableField("route_key")
    private String routeKey;

    /**
     * 第三方回调是否已处理 0-未处理 1-已处理
     */
    @Schema(title = "第三方回调是否已处理 0-未处理 1-已处理")
    @TableField("third_party_notify_status")
    private Integer thirdPartyNotifyStatus;

    /**
     * 业务方是否已正确处理  0-未处理 1-已处理
     */
    @Schema(title = "业务方是否已正确处理  0-未处理 1-已处理")
    @TableField("business_notify_status")
    private Integer businessNotifyStatus;

    /**
     * 交易状态：1（交易创建，等待买家付款）、2（未付款交易超时关闭）、3（交易支付成功）
     */
    @Schema(title = "交易状态：1（交易创建，等待买家付款）、2（未付款交易超时关闭）、3（交易支付成功）")
    @TableField("trade_status")
    private Integer tradeStatus;

    /**
     * 回调次数
     */
    @Schema(title = "回调次数")
    @TableField("third_party_notify_number")
    private Integer thirdPartyNotifyNumber;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

}
