package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 开卡方式：付费购买集合
 * @Author zhaokw
 * @Date 21:19 2024\5\15 0015
 **/
@Data
@Schema(title = "开卡方式：付费购买集合")
@Accessors(chain = true)
public class PaymentVo {

    @Schema(title = "会员卡id")
    private Long id;

    @Schema(title = "会员卡等级代码")
    private String rankCode;

    @Schema(title = "会员卡等级名称")
    private String rankName;

    @Schema(title = "付费金额")
    private BigDecimal payAmount;

    @Schema(title = "会员权益")
    private List<MemberPrivilegeVo> memberPrivilegeVos;

    @Schema(title = "会员卡风格类型 1-系统风格 2-自定义风格")
    private Integer style;

    @Schema(title = "会员卡图片url")
    private String backGroundPicUrl;

    @Schema(title = "特权开关 0-关闭 1-开启")
    private Integer isOpen;

    @Schema(title = "会员卡权益介绍")
    private String introduce;

}
