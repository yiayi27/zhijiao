package com.medusa.gruul.payment.api.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.payment.api.enums.CheckNameEnum;
import com.medusa.gruul.payment.api.enums.PayChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @author create by zq
 * @date created in 2024/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_ent_pay")
@Schema(title = "EntPay对象", description = "EntPay实体")
public class EntPay extends BaseEntity {

    /**
     * 主键id
     */
    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 支付渠道:1-微信
     */
    @Schema(title = "支付渠道:1-微信")
    @TableField("pay_channel")
    private PayChannelEnum payChannel;


    /**
     * 支付渠道类型
     */
    @Schema(title = "支付渠道类型")
    @TableField("pay_channel_type")
    private PayChannelEnum payChannelType;


    /**
     * 订单总金额，单位为分
     */
    @Schema(title = "订单总金额，单位为分")
    @TableField("amount")
    private Integer amount;


    /**
     * 企业付款备注
     */
    @Schema(title = "企业付款备注")
    @TableField("description")
    private String description;


    /**
     * 附加数据,格式为json字符串,怎么发送怎么返回
     */
    @Schema(title = "附加数据,格式为json字符串,怎么发送怎么返回")
    @TableField("business_params")
    private String businessParams;


    /**
     * 支付流水号
     */
    @Schema(title = "支付流水号")
    @TableField("transaction_id")
    private String transactionId;


    /**
     * 交易状态：1、 发起支付 2、 交易支付成功 3、交易支付失败 4、交易同步返回成功单业务未成功
     */
    @Schema(title = "交易状态：1、 发起支付 2、 交易支付成功 3、交易支付失败 4、交易同步返回成功单业务未成功")
    @TableField("trade_status")
    private String tradeStatus;


    /**
     * 用户标识
     */
    @Schema(title = "用户标识")
    @TableField("openid")
    private String openid;


    /**
     * 校验用户姓名选项
     */
    @Schema(title = "校验用户姓名选项")
    @TableField("check_name")
    private CheckNameEnum checkName;


    /**
     * 收款用户姓名. 可选
     */
    @Schema(title = "收款用户姓名.")
    @TableField("re_user_name")
    private String reUserName;


    /**
     * 调用接口的机器Ip地址
     */
    @Schema(title = "调用接口的机器Ip地址")
    @TableField("spbill_create_ip")
    private String spbillCreateIp;


    /**
     * 支付成功时间
     */
    @Schema(title = "支付成功时间")
    @TableField("pay_time")
    private String payTime;

}
