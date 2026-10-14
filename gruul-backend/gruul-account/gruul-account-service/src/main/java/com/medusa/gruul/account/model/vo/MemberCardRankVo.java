package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员卡等级
 *
 * @author zhaokw
 */
@Data
@Schema(title = "会员卡等级")
public class MemberCardRankVo {

    @Schema(title = "会员卡等级代码")
    private String rankCode;


    @Schema(title = "会员卡等级名称")
    private String rankName;
}
