package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @data: 2024/2/22
 */
@Data
public class UseMemberCardRankVo {

    /**
     * 会员卡等级代码
     */
    @Schema(title = "会员卡等级代码")
    private String rankCode;

    /**
     * 会员卡名称
     */
    @Schema(title = "会员卡名称")
    private String rankName;
}
