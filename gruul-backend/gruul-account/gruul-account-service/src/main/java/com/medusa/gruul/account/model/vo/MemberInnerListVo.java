package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description 会员列表返回值
 * @Author zhaokw
 * @Date 23:43 2024\7\25 0009
 **/
@Data
@Schema(title = "会员列表返回值")
@Accessors(chain = true)
public class MemberInnerListVo {

    @Schema(title = "会员id")
    private Long id;

    @Schema(title = "会员卡编号")
    private String memberNumber;

    @Schema(title = "会员注册时间")
    private LocalDateTime memberRegisterTime;

    @Schema(title = "会员到期时间")
    private LocalDateTime memberExpireTime;

    @Schema(title = "成为会员天数")
    private Long becomeMemberDayNumber;

    @Schema(title = "会员等级代码")
    private String rankCode;

    @Schema(title = "会员等级名称")
    private String rankName;

    @Schema(title = "开卡方式")
    private Integer openType;

    @Schema(title = "会员卡名称")
    private String title;

    @Schema(title = "开卡条件")
    private BigDecimal openValue;

    @Schema(title = "注册表单信息")
    private String registerData;

}
