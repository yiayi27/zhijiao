package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/2/28
 */
public class JumpDetailVo {

    @Schema(title = "会员功能是否开启 0-关闭  1-开启")
    private Integer memberOpen;
    @Schema(title = "当前是否会员   0-不是  1-是")
    private Integer whetherMember;
    @Schema(title = "是否购买过会员 0-不是  1-是")
    private Integer historyBuy;
}
