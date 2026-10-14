package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 订单设置表
 * </p>
 *
 * @author alan
 * @since 2023-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_setting")
@Schema(title = "OrderSetting对象", description = "订单设置表")
public class OrderSetting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String shopId;

    /**
     * 秒杀订单超时关闭时间(分)
     */
    @Schema(title = "秒杀订单超时关闭时间(分)")
    @TableField("flash_order_overtime")
    private Long flashOrderOvertime;

    /**
     * 正常订单超时时间(分)
     */
    @Schema(title = "正常订单超时时间(分)")
    @TableField("normal_order_overtime")
    private Long normalOrderOvertime;

    /**
     * 发货后自动确认收货时间（天）
     */
    @Min(0)
    @Max(30)
    @Schema(title = "发货后自动确认收货时间（天）")
    @TableField("confirm_overtime")
    private Integer confirmOvertime;

    /**
     * 自动完成交易时间，不能申请售后（天）
     */
    @Min(0)
    @Max(30)
    @Schema(title = "自动完成交易时间，不能申请售后（天）")
    @TableField("finish_overtime")
    private Integer finishOvertime;

    /**
     * 未启用
     * 订单完成后自动好评时间（天）
     */
    @Min(0)
    @Max(30)
    @Schema(title = "订单完成后自动好评时间（天）")
    @TableField("comment_overtime")
    private Integer commentOvertime;

    /**
     * 是否开启订单评论
     */
    @Schema(title = "是否开启订单评论")
    @TableField("open_evaluate")
    private Boolean openEvaluate;


    /**
     * 最大售后申请次数
     */
    @Schema(title = "最大售后申请次数")
    @TableField("afs_apply_number")
    private Integer afsApplyNumber;


    /**
     * 商家最大审核期限(天)
     */
    @Min(0)
    @Max(30)
    @Schema(title = "商家最大审核期限(天)")
    @TableField("merchant_confirm_overtime")
    private Integer merchantConfirmOvertime;


    /**
     * 用户最大退货期限(天)
     */
    @Min(0)
    @Max(30)
    @Schema(title = "用户最大退货期限(天)")
    @TableField("user_return_overtime")
    private Integer userReturnOvertime;


    /**
     * 快递AppId
     */
    @Schema(title = "快递AppId")
    @TableField("kd_app_id")
    private String kdAppId;

    /**
     * 快递AppKey
     */
    @Schema(title = "快递AppKey")
    @TableField("kd_app_key")
    private String kdAppKey;

    /**
     * 支付方式
     */
    @NotBlank
    @Schema(title = "支付方式：1->微信支付;2->余额支付;3->好友代付;")
    @TableField("payment_model")
    private String paymentModel;

    /**
     * 自定义表单
     */
    @Schema(title = "自定义表单")
    @TableField("custom_from")
    private String customFrom;

    /**
     * 积分抵扣比例百分比
     */
    @NotNull
    @Min(0)
    @Schema(title = "积分抵扣比例百分比")
    @TableField("ratio")
    private BigDecimal ratio;

    /**
     * 顶底抵扣上限额度百分比
     */
    @NotNull
    @Min(0)
    @Schema(title = "顶底抵扣上限额度百分比")
    @TableField("ceiling")
    private BigDecimal ceiling;

    /**
     * 是否开启下单通知：0->关闭；1->开启
     */
    @NotNull
    @Schema(title = "是否开启下单通知：0->关闭；1->开启")
    @TableField("order_notify")
    private Boolean orderNotify;

    /**
     * 快递100客户key
     */
    @TableField("kd_hundred_key")
    private String kdHundredKey;

    /**
     * 快递100客户密匙
     */
    @TableField("kd_hundred_secret")
    private String kdHundredSecret;

    /**
     * 快递100打印机编码
     */
    @TableField("kd_hundred_print_no")
    private String kdHundredPrintNo;

    /**
     * 快递电子面单密钥
     */
    @TableField("kd_app_secret")
    private String kdAppSecret;

    /**
     * 快递100账户授权码
     */
    @TableField("kd_hundred_customer")
    private String kdHundredCustomer;

}
