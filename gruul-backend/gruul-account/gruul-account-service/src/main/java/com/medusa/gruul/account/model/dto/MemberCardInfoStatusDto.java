package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 会员卡功能关闭启用请求参数
 *
 * @author zhaokw
 */
@Data
@Accessors(chain = true)
public class MemberCardInfoStatusDto {

    @Schema(title = "会员功能开关")
    @NotNull(message = "会员功能开关不能为空 0代表关闭 1代表打开")
    private Integer memberCardInfoSwitch;

    @Schema(title = "店铺id")
    private String shopId;

    @Schema(title = "会员模块名：0-总开关 1-储值开关")
    private Integer memberModel;

}
