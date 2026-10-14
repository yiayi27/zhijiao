package com.medusa.gruul.finance.model;

import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import com.medusa.gruul.finance.api.enums.ChannelTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Api withdrawal brokerage dto.
 *
 * @description: 小程序端佣金提现申请
 * @author: alan
 * @date: 2023/11/5 21:29
 */
@Data
@Schema(title = "小程序端佣金提现申请")
public class ApiWithdrawalBrokerageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 金额
     */
    @DecimalMin(value = "0.3", message = "最低金额不得小于0.3元")
    @NotNull
    @Schema(title = "金额")
    private BigDecimal amount;

    /**
     * 提现渠道:0->微信；1->支付宝；2->银行卡
     */
    @NotNull
    @Schema(title = "提现渠道:0->微信；1->支付宝；2->银行卡", example = "0")
    private ChannelTypeEnum channel;

    /**
     * 用户类型：3->用户
     */
    @NotNull
    @Schema(title = "用户类型：3->用户", example = "0")
    private AccountTypeEnum accountType;

    /**
     * 对应类型的用户ID
     */
    @NotBlank
    @Schema(title = "对应类型的用户ID")
    private String userId;

    /**
     * 姓名
     */
    @Schema(title = "姓名")
    private String name;

    /**
     * 手机
     */
    @Schema(title = "手机")
    private String phone;

    /**
     * 账号
     */
    @Schema(title = "账号")
    private String account;

    /**
     * 开户行
     */
    @Schema(title = "开户行")
    private String bankName;

}
