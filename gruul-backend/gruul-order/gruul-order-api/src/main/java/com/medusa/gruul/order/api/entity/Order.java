package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import com.medusa.gruul.order.api.enums.PayTypeEnum;
import com.medusa.gruul.order.api.enums.SourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author alan
 * @since 2023-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
@Schema(title = "Order对象", description = "订单表")
public class Order extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @Schema(title = "订单id")
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 用户openId
     */
    @Schema(title = "用户openId")
    @TableField("open_id")
    private String openId;

    /**
     * 用户帐号
     */
    @Schema(title = "用户帐号")
    @TableField("user_name")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    @TableField("user_avatar_url")
    private String userAvatarUrl;

    /**
     * 用户备注
     */
    @Schema(title = "用户备注")
    @TableField("user_note")
    private String userNote;

    /**
     * 订单类型
     */
    @Schema(title = "订单类型")
    @TableField("type")
    private OrderTypeEnum type;

    /**
     * 订单总金额=商品指导价*商品数量
     */
    @Schema(title = "订单总金额")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 实际金额=应付金额-退款金额
     */
    @Schema(title = "实际金额")
    @TableField("discounts_amount")
    private BigDecimal discountsAmount;

    /**
     * 应付金额（实际支付金额）=订单总金额+运费-促销优化金额
     */
    @Schema(title = "应付金额（实际支付金额）")
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 运费金额=快递运费或送货上门的费用
     */
    @Schema(title = "运费金额")
    @TableField("freight_amount")
    private BigDecimal freightAmount;

    /**
     * 促销优化金额=积分抵扣+优惠券+满减+会员价+会员免运费
     */
    @Schema(title = "促销优化金额（促销价、满减、会员价）")
    @TableField("promotion_amount")
    private BigDecimal promotionAmount;

    /**
     * 使用的积分
     */
    @Schema(title = "使用的积分")
    @TableField("integration")
    private BigDecimal integration;

    /**
     * 积分抵扣金额
     */
    @Schema(title = "积分抵扣金额")
    @TableField("integration_amount")
    private BigDecimal integrationAmount;

    /**
     * 优惠券id
     */
    @Schema(title = "优惠券id")
    @TableField("coupon_id")
    private Long couponId;

    /**
     * 优惠券抵扣金额
     */
    @Schema(title = "优惠券抵扣金额")
    @TableField("coupon_amount")
    private BigDecimal couponAmount;

    /**
     * 接龙活动ID
     */
    @Schema(title = "接龙活动ID")
    @TableField("solitaire_activity_id")
    private Long solitaireActivityId;

    /**
     * 满减活动ID
     */
    @Schema(title = "满减活动ID")
    @TableField("full_scale_id")
    private Long fullScaleId;

    /**
     * 满减优惠金额
     */
    @Schema(title = "满减优惠金额")
    @TableField("full_scale_amount")
    private BigDecimal fullScaleAmount;

    /**
     * 会员总优惠金额
     */
    @Schema(title = "会员总优惠金额")
    @TableField("member_amount")
    private BigDecimal memberAmount;

    /**
     * 支付方式
     */
    @Schema(title = "支付方式")
    @TableField("pay_type")
    private PayTypeEnum payType;

    /**
     * 支付流水号
     */
    @Schema(title = "支付流水号")
    @TableField("transaction_id")
    private String transactionId;

    /**
     * 支付时间
     */
    @Schema(title = "支付时间")
    @TableField("pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 订单来源
     */
    @Schema(title = "订单来源")
    @TableField("source_type")
    private SourceTypeEnum sourceType;

    /**
     * 订单状态
     */
    @Schema(title = "订单状态")
    @TableField("original_status")
    private OrderStatusEnum originalStatus;

    /**
     * 订单状态
     */
    @Schema(title = "订单状态")
    @TableField("status")
    private OrderStatusEnum status;

    /**
     * 关闭时间
     */
    @Schema(title = "关闭时间")
    @TableField("close_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closeTime;

    /**
     * 赠送的积分
     */
    @Schema(title = "赠送的积分")
    @TableField("gift_integration")
    private BigDecimal giftIntegration;

    /**
     * 订单备注
     */
    @Schema(title = "订单备注")
    @TableField("note")
    private String note;

    /**
     * 评价时间
     */
    @Schema(title = "评价时间")
    @TableField("comment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentTime;

    /**
     * 完成时间
     */
    @Schema(title = "完成时间")
    @TableField("complete_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;

    /**
     * 预计到货时间
     */
    @Schema(title = "预计到货时间")
    @TableField("estimated_delivery_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;

    /**
     * 过期时间
     */
    @Schema(title = "过期时间")
    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 参与的活动
     */
    @Schema(title = "参与的活动")
    @TableField("activity_ids")
    private String activityIds;

    /**
     * 开通会员的订阅消息ID
     */
    @Schema(title = "开通会员的订阅消息ID")
    @TableField("member_template_id")
    private String memberTemplateId;

    /**
     * 自定义字段:[{"key":"IDCard","value":"332022199001010011"},{"key":"Phone","value":"13956852259"}]
     */
    @Schema(title = "自定义字段:[{\"key\":\"IDCard\",\"value\":\"332022199001010011\"},{\"key\":\"Phone\"," +
            "\"value\":\"13956852259\"}]")
    @TableField("custom_form")
    private String customForm;

    /**
     * 享受的权益
     */
    @Schema(title = "享受的权益，多个以逗号分割： 0-自定义权益 1-商品权益 2-包邮权益 3-积分倍数权益 4-专属客服权益 5-优先发货 6-急速售后 ")
    @TableField("privileges")
    private String privileges;

    /**
     * 返利金额
     */
    @Schema(title = "返利金额")
    @TableField("rebate_amount")
    private BigDecimal rebateAmount;

    /**
     * 一级分销金额
     */
    @Schema(title = "一级分销金额")
    @TableField("first_distribution_amount")
    private BigDecimal firstDistributionAmount;

    /**
     * 二级分销金额
     */
    @Schema(title = "二级分销金额")
    @TableField("second_distribution_amount")
    private BigDecimal secondDistributionAmount;

    /**
     * 退款金额
     */
    @Schema(title = "退款金额")
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    /**
     * 退款支付流水号
     */
    @Schema(title = "退款支付流水号")
    @TableField("refund_transaction_id")
    private String refundTransactionId;


}
