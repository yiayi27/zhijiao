package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 会员中心返回值
 * @Author zhaokw
 * @Date 23:43 2024\6\9 0009
 **/
@Data
@Schema(title = "会员中心返回值")
@Accessors(chain = true)
public class MemberCenterVo {

    @Schema(title = "账户总余额")
    private BigDecimal accountBalance;

    @Schema(title = "账户可用余额,根据返利设置判断")
    private BigDecimal accountUseBalance;

    @Schema(title = "返利余额")
    private BigDecimal rebateBonus;

    @Schema(title = "账户可用积分")
    private BigDecimal useAmount;

    @Schema(title = "充值开关 0-关 1-开")
    private Integer state;

    @Schema(title = "当前用户账户会员卡信息")
    private List<MemberCardInfoCenterVo> memberCardInfoCenterVos;

    @Schema(title = "当前开卡方式，会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换 ")
    private Integer openType;

}
