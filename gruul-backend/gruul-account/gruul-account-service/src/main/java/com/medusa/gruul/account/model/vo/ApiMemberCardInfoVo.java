package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description 小程序端会员列表主页返回值
 * @Author zhaokw
 * @Date 11:14 2024\5\30 0030
 **/
@Data
@Schema(title = "小程序端会员列表主页返回值")
@Accessors(chain = true)
public class ApiMemberCardInfoVo {

    @Schema(title = "会员id")
    private Long id;

    @Schema(title = "会员卡Id")
    private Long memberCardInfoId;

    @Schema(title = "店铺用户id")
    private String userId;

    @Schema(title = "用户余额")
    private BigDecimal supplyBonus;

    @Schema(title = "用户返利余额")
    private BigDecimal rebateBonus;

    @Schema(title = "用户余额+用户返利余额总额")
    private BigDecimal supplyAndRebateBonus;

    @Schema(title = "会员等级代码")
    private String rankCode;

    @Schema(title = "会员等级名称")
    private String rankName;

    @Schema(title = "小程序端主页状态：1-PC端开启会员-当前用户是会员，2-PC端开启会员-当前用户不是会员，3-PC端关闭会员-当前用户是会员，4-PC端关闭会员-当前用户不是会员")
    private Integer status;

    @Schema(title = "会员到期时间")
    private LocalDateTime memberExpireTime;

    @Schema(title = "会员卡注册状态 0未注册,1已注册  2-会员到期")
    private Integer state;

}
