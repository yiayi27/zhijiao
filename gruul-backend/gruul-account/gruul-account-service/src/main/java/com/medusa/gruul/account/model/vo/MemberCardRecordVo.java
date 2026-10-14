package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description 会员卡记录返回值
 * @Author zhaokw
 * @Date 23:43 2024\6\10 0009
 **/
@Data
@Schema(title = "会员卡记录返回值")
@Accessors(chain = true)
public class MemberCardRecordVo {

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
    /**
     * 会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换
     */
    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换 ")
    private Integer openType;

    /**
     * 开通所需值(付费金额或所需积分),累计消费满多少金额
     */
    @Schema(title = "开通所需值(付费金额或所需积分),累计消费满多少金额")
    private BigDecimal openValue;

}
