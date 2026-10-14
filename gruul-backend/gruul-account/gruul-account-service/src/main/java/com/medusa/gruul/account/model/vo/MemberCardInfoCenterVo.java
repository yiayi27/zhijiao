package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description 当前用户会员卡信息返回值
 * @Author zhaokw
 * @Date 23:43 2024\6\9 0009
 **/
@Data
@Schema(title = "当前用户会员卡信息返回值")
@Accessors(chain = true)
public class MemberCardInfoCenterVo {

    /**
     * 会员id
     */
    @Schema(title = "会员id ")
    private Long id;

    /**
     * 会员卡编号
     */
    @Schema(title = "会员卡编号")
    private String memberNumber;

    /**
     * 会员卡注册状态 0未注册,1已注册 2-会员到期
     */
    @Schema(title = "会员卡注册状态 0未注册,1已注册  2-会员到期")
    private Integer state;

    /**
     * 会员卡Id
     */
    @Schema(title = "会员卡Id")
    private Long memberCardInfoId;

    /**
     * 会员注册时间
     */
    @Schema(title = "会员注册时间")
    private LocalDateTime memberRegisterTime;

    /**
     * 会员到期时间
     */
    @Schema(title = "会员到期时间")
    private LocalDateTime memberExpireTime;

    @Schema(title = "会员卡名称")
    private String title;

    @Schema(title = "会员卡等级代码")
    private String rankCode;

    @Schema(title = "会员卡等级名称")
    private String rankName;

    @Schema(title = "会员权益")
    private List<MemberPrivilegeVo> memberCardPrivileges;

    /**
     * 会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换
     */
    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换")
    private Integer openType;

    /**
     * 开通所需值(付费金额或所需积分),累计消费满多少金额
     */
    @Schema(title = "开通所需值(付费金额或所需积分),累计消费满多少金额")
    private BigDecimal openValue;

    /**
     * 使用期限 按天计算 0表示不限制
     */
    @Schema(title = "使用期限 按天计算  0表示不限制")
    private Long useInDate;

    @Schema(title = "会员卡图片url")
    private String backGroundPicUrl;

    @Schema(title = "实际支付金额/实际支付积分")
    private BigDecimal actualOpenValue;

    /**
     * 特权开关 0-关闭 1-开启
     */
    @Schema(title = "特权开关 0-关闭 1-开启")
    private Integer isOpen;

    /**
     * 是否是最高等级
     */
    @Schema(title = "1-是 2 否")
    private Integer mostRankCode;

    @Schema(title = "剩余时长")
    private String remainDate;


}
