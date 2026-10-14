package com.medusa.gruul.finance.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import com.medusa.gruul.finance.api.enums.AuditStatusEnum;
import com.medusa.gruul.finance.api.enums.ChannelTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 提现记录
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_finance_withdrawal_record")
@Schema(title = "FinanceWithdrawalRecord对象", description = "提现记录")
public class FinanceWithdrawalRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
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
     * 用户id
     */
    @Schema(title = "用户id")
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
     * 用户类型：3->用户
     */
    @Schema(title = "用户类型：3->用户")
    @TableField("account_type")
    private AccountTypeEnum accountType;

    /**
     * 金额
     */
    @Schema(title = "金额")
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 提现渠道:0->微信；1->支付宝；2->银行卡
     */
    @Schema(title = "提现渠道:0->微信；1->支付宝；2->银行卡; 3->线下结算")
    @TableField("channel")
    private ChannelTypeEnum channel;

    /**
     * 手机
     */
    @Schema(title = "手机")
    @TableField("phone")
    private String phone;

    /**
     * 姓名
     */
    @Schema(title = "姓名")
    @TableField("name")
    private String name;

    /**
     * 扩展姓名
     */
    @Schema(title = "扩展姓名")
    @TableField("ext_name")
    private String extName;

    /**
     * openId
     */
    @Schema(title = "openId")
    @TableField("open_id")
    private String openId;

    /**
     * 账号
     */
    @Schema(title = "账号")
    @TableField("account")
    private String account;

    /**
     * 开户行
     */
    @Schema(title = "开户行")
    @TableField("bank_name")
    private String bankName;

    /**
     * 交易流水号
     */
    @Schema(title = "交易流水号")
    @TableField("trade_no")
    private String tradeNo;

    /**
     * 到账时间
     */
    @Schema(title = "到账时间")
    @TableField("arrive_time")
    private LocalDateTime arriveTime;

    /**
     * 审核状态:1->总店待审核；2->已通过；3->已拒绝
     */
    @Schema(title = "审核状态:1->总店待审核；2->已通过；3->已拒绝")
    @TableField("audit_status")
    private AuditStatusEnum auditStatus;

    /**
     * 备注
     */
    @Schema(title = "备注")
    @TableField("note")
    private String note;

    /**
     * 拒绝说明
     */
    @Schema(title = "拒绝说明")
    @TableField("reason")
    private String reason;

    /**
     * 提现手续费
     */
    @Schema(title = "提现手续费")
    @TableField("commission")
    private BigDecimal commission;

    /**
     * 流水号
     */
    @Schema(title = "流水号")
    @TableField("sn")
    private String sn;

}


