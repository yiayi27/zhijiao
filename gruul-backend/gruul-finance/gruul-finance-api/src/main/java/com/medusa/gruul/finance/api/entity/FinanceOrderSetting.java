package com.medusa.gruul.finance.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 订单设置
 * </p>
 *
 * @author alan
 * @since 2024-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_finance_order_setting")
@Schema(title = "FinanceOrderSetting对象", description = "订单设置")
public class FinanceOrderSetting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @NotNull
    @Schema(title = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 申请售后时间（日）
     */
    @NotNull(message = "申请售后时间不能为空")
    @Min(value = 1, message = "申请售后时间不能小于1")
    @Schema(title = "申请售后时间（日）")
    @TableField("support_allowed_day")
    private Integer supportAllowedDay;

    /**
     * 售后审核时间（日）
     */
    @NotNull(message = "售后审核时间不能为空")
    @Min(value = 1, message = "售后审核时间不能小于1")
    @Schema(title = "售后审核时间（日）")
    @TableField("support_audit_day")
    private Integer supportAuditDay;

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
    @NotNull(message = "支付方式不能为空")
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
    @NotNull(message = "抵扣比例不能为空")
    @Min(value = 0, message = "抵扣比例不能小于0")
    @Schema(title = "积分抵扣比例百分比")
    @TableField("ratio")
    private BigDecimal ratio;

    /**
     * 顶底抵扣上限额度百分比
     */
    @NotNull(message = "上限额度不能为空")
    @Min(value = 0, message = "上限额度不能小于0")
    @Schema(title = "顶底抵扣上限额度百分比")
    @TableField("ceiling")
    private BigDecimal ceiling;

    /**
     * 是否开启下单通知：0->关闭；1->开启
     */
    @NotNull(message = "是否开启下单通知不能为空")
    @Schema(title = "是否开启下单通知：0->关闭；1->开启")
    @TableField("order_notify")
    private Boolean orderNotify;


}
