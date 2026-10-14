package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 更新会员的到期日，续期
 *
 * @author zhaokw
 */
@Data
@Accessors(chain = true)
public class UpdateMemberExpireTimeDto {

    @Schema(title = "会员卡编号")
    @NotBlank(message = "会员卡编号为空")
    private String memberNumber;

    @Schema(title = "会员到期日")
    private String memberExpireTime;

    @Schema(title = "续费天数")
    private Long date;
}
