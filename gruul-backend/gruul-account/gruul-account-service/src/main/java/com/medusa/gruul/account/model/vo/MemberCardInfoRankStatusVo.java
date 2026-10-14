package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Description 会员卡各个等级关闭启用参数
 * @Author zhaokw
 * @Date 0:05 2024\5\19 0019
 **/
@Data
@Schema(title = "会员卡各个等级关闭启用参数")
@Accessors(chain = true)
public class MemberCardInfoRankStatusVo {

    @Schema(title = "会员卡等级功能开关")
    private Integer memberCardInfoSwitch;

    @Schema(title = "会员卡等级代码")
    private String rankCode;

    @Schema(title = "店铺id")
    private String shopId;

    @Schema(title = "更新时间")
    private Date updateTime;

}
