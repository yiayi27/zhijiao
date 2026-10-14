package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 升级会员等级
 *
 * @author zhaokw
 */
@Data
@Accessors(chain = true)
public class UpgradeMemberRankDto {

    @Schema(title = "会员卡编号")
    @NotBlank(message = "会员卡编号为空")
    private String memberNumber;

    @Schema(title = "会员等级代码")
    private String rankCode;

    @Schema(title = "会员等级名称")
    private String rankName;

    @Schema(title = "店铺id")
    private String shopId;


}
