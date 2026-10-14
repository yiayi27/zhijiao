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
public class MemberCardUpgradeVo {

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "会员卡编号")
    private String memberNumber;

    /**
     * 用户名称
     */
    @Schema(title = "用户名称")
    private String nikeName;

    /**
     * 头像url
     */
    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "会员卡等级代码")
    private String rankCode;

    @Schema(title = "会员卡等级名称")
    private String rankName;

    @Schema(title = "剩余时长")
    private String remainDate;

    /**
     * 开通所需值(付费金额或所需积分),累计消费满多少金额
     */
    @Schema(title = "开通所需值(付费金额或所需积分),累计消费满多少金额")
    private BigDecimal openValue;

    /**
     * 会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换
     */
    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换 ")
    private Integer openType;

    /**
     * 使用期限 按天计算 0表示不限制
     */
    @Schema(title = "使用期限 按天计算  0表示不限制")
    private Long useInDate;

    @Schema(title = "账户可用余额")
    private BigDecimal accountBalance;

    @Schema(title = "账户可用积分")
    private BigDecimal useAmount;

    @Schema(title = "当前店铺账户会员卡信息")
    private List<MemberCardInfoCenterVo> memberCardInfoCenterVos;

}
