package com.medusa.gruul.account.api.entity;

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

/**
 * <p>
 * 账户余额明细表
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_balance_record")
@Schema(title = "MemberBalanceRecord对象", description = "账户余额明细表")
public class MemberBalanceRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 使用余额
     */
    @Schema(title = "使用余额")
    @TableField("use_balance")
    private BigDecimal useBalance;

    /**
     * 剩余余额
     */
    @Schema(title = "剩余余额")
    @TableField("remaining_balance")
    private BigDecimal remainingBalance;

    /**
     * 用户店铺id
     * 老数据以会员卡id存储，老数据该值可能不存在值，需要兼容
     */
    @Schema(title = "用户店铺id")
    @TableField("shop_user_id")
    private Long shopUserId;


    /**
     * 会员id
     */
    @Schema(title = "会员id")
    @TableField("member_info_id")
    private Long memberInfoId;

    /**
     * 变化类型 0-添加 1-减少
     */
    @Schema(title = "变化类型 0-添加 1-减少")
    @TableField("change_type")
    private Integer changeType;

    /**
     * 明细名
     */
    @Schema(title = "明细名")
    @TableField("detail_name")
    private String detailName;

    /**
     * 订单id
     */
    @Schema(title = "订单id")
    @TableField("order_id")
    private Long orderId;

    /**
     * 交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功 4-商品分销 5-提现
     */
    @Schema(title = "交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功 4-商品分销 5-提现 6-用户充值")
    @TableField("deal_type")
    private Integer dealType;

    /**
     * 订单来源：1-会员，2-商品，3-积分
     */
    @Schema(title = "订单来源：1-会员，2-商品，3-积分")
    @TableField("source")
    private Integer source;

    /**
     * 外部订单id
     */
    @Schema(title = "外部订单id")
    @TableField("out_order_id")
    private Long outOrderId;

    /**
     * 用户删除记录状态
     */
    @Schema(title = "用户删除记录状态  0-未删除 1-已删除")
    @TableField("account_del")
    private Boolean accountDel;

    /**
     * 订单应付金额（实际支付金额）
     */
    @Schema(title = "订单应付金额（实际支付金额）")
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 订单归属用户id
     */
    @Schema(title = "订单归属用户id")
    @TableField("order_user_id")
    private Long orderUserId;

    /**
     * 分销类型（1-一级分销 2-二级分销 注：商品分销专用）
     */
    @Schema(title = "分销类型（1-一级分销 2-二级分销 注：商品分销专用）")
    @TableField("distribution_type")
    private Integer distributionType;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;


}
