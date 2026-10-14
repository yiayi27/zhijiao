package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 小程序升级会员等级
 *
 * @author zhaokw
 */
@Data
@Accessors(chain = true)
public class UpgradeApiMemberRankDto {

    @Schema(title = "会员卡编号")
    @NotBlank(message = "会员卡编号为空")
    private String memberNumber;

    @Schema(title = "会员等级代码")
    private String rankCode;

    @Schema(title = "会员等级名称")
    private String rankName;

    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换")
    private Integer openType;

    @Schema(title = "支付方式：1-微信支付 2-余额支付")
    private Integer paymentType;

    @Schema(title = "openType为1时，表示扣除金额；openType为2时，表示当前用户扣除积分")
    private BigDecimal availableAmount;

    @Schema(title = "openType为1时，表示当前用户可用余额；openType为2时，表示当前用户可用积分")
    private BigDecimal useAmount;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "店铺ID")
    private String shopId;
}
