package com.medusa.gruul.afs.api.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.afs.api.enums.AfsOrderCloseTypeEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderStatusEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderTypeEnum;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 售后工单
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_afs_order")
@Schema(title = "AfsOrder对象", description = "售后工单")
public class AfsOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 工单id
     */
    @Schema(title = "工单id")
    @TableId("id")
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 工单编号
     */
    @Schema(title = "工单编号")
    @TableField("no")
    private String no;

    /**
     * 工单类型
     */
    @Schema(title = "工单类型：POINT_EXCHANGE->提货点换货;POINT_REFUND->提货点退款;REFUND->退款;" +
            "EXCHANGE->换货;RETURN_REFUND->退货退款;")
    @TableField("type")
    private AfsOrderTypeEnum type;

    /**
     * 审核状态
     */
    @Schema(title = "审核状态：WAIT_FOR_BUSINESS_APPROVED->待商家审核;" +
            "WAIT_FOR_RETURN->待退货; " +
            "WAIT_FOR_SEND->待发货;" +
            "WAIT_FOR_PICKUP->待提货;" +
            "SHIPPED->配送中;" +
            "SUCCESS->成功;" +
            "CLOSE->已关闭")
    @TableField("status")
    private AfsOrderStatusEnum status;

    /**
     * 关闭原因
     */
    @Schema(title = "关闭原因：USER_CANCEL->用户撤销;POINT_CANCEL->提货点撤销;POINT_NOT_RECEIVED->提货点未收到退货;" +
            "RE_EXCHANGE->重新申请换货;SELLER_REFUSE->卖家拒绝")
    @TableField("close_type")
    private AfsOrderCloseTypeEnum closeType;

    /**
     * 申请人ID
     */
    @Schema(title = "申请人ID")
    @TableField("user_id")
    private String userId;

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
     * 商品sku编号
     */
    @Schema(title = "商品sku编号")
    @TableField("product_sku_id")
    private Long productSkuId;

    /**
     * 消息订阅ID
     */
    @Schema(title = "消息订阅ID	")
    @TableField("template_id")
    private String templateId;

    /**
     * 退货消息订阅ID
     */
    @Schema(title = "退货消息订阅ID	")
    private String returnTemplateId;

    /**
     * 售后生成的订单
     */
    @Schema(title = "售后生成的订单")
    @TableField("order_id")
    private Long orderId;


    /**
     * 退款金额
     */
    @Schema(title = "退款金额")
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    /**
     * 过期时间
     */
    @Schema(title = "过期时间")
    @TableField(value = "deadline", updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    /**
     * 说明
     */
    @Schema(title = "说明")
    @TableField("description")
    private String description;

    /**
     * 照片
     */
    @Schema(title = "照片")
    @TableField("images")
    private String images;

    /**
     * 关闭时间
     */
    @Schema(title = "关闭时间")
    @TableField("close_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closeTime;

    /**
     * 拒绝时间
     */
    @Schema(title = "拒绝时间")
    @TableField("refusal_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refusalTime;

    /**
     * 成功时间
     */
    @Schema(title = "成功时间")
    @TableField("success_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime successTime;

    /**
     * 拒绝原因
     */
    @Schema(title = "拒绝原因")
    @TableField("refusal_reason")
    private String refusalReason;

    /**
     * 申请的原因
     */
    @Schema(title = "申请的原因")
    @TableField("reason")
    private String reason;

    /**
     * 申请售后的签收单ID
     */
    @Schema(title = "申请售后的签收单ID")
    @TableField("receipt_bill_id")
    private Long receiptBillId;

    /**
     * 备注
     */
    @Schema(title = "备注")
    @TableField("note")
    private String note;

    /**
     * 物流单号
     */
    @Schema(title = "物流单号")
    @TableField("delivery_sn")
    private String deliverySn;

    /**
     * 物流公司编号
     */
    @Schema(title = "物流公司编号")
    @TableField("delivery_code")
    private String deliveryCode;

    /**
     * 物流公司
     */
    @Schema(title = "物流公司")
    @TableField("delivery_company")
    private String deliveryCompany;

    /**
     * 是否物流配送
     */
    @Schema(title = "是否物流配送")
    @TableField("is_logistics")
    private Boolean isLogistics;


}
