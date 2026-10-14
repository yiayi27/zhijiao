package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 会员卡各个等级关闭启用请求参数
 *
 * @author zhaokw
 */
@Data
@Accessors(chain = true)
public class MemberCardInfoRankStatusDto {

    @Schema(title = "会员卡等级功能开关")
    @NotNull(message = "会员等级开关不能为空 0代表关闭 1代表打开")
    private Integer memberCardInfoSwitch;

    @Schema(title = "会员卡等级代码")
    private String rankCode;


}
