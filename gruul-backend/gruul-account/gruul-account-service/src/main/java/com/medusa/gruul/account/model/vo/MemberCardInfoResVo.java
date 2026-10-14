package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Description 购买会员卡成功的返回集合
 * @Author zhaokw
 * @Date 11:42 2024\6\7 0007
 **/
@Data
@Schema(title = "购买会员卡成功的返回集合")
@Accessors(chain = true)
public class MemberCardInfoResVo {

    @Schema(title = "会员id")
    private Long memberId;

    @Schema(title = "会员卡id")
    private Long memberCardId;

    @Schema(title = "会员卡等级代码")
    private String rankCode;

    @Schema(title = "会员卡等级名称")
    private String rankName;

    @Schema(title = "会员卡名称")
    private String title;

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

    @Schema(title = "会员卡使用期限 按天计算 0表示不限制")
    private Long useInDate;

    /**
     * 会员卡编号
     */
    @Schema(title = "会员卡编号")
    private String memberNumber;


}
