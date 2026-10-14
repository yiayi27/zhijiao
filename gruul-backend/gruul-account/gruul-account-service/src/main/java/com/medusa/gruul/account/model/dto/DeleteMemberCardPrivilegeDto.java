package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 删除会员卡权益请求参数
 *
 * @author zhaokw
 */
@Data
@Accessors(chain = true)
public class DeleteMemberCardPrivilegeDto {

    @Schema(title = "会员权益id")
    @NotNull(message = "会员权益id为空")
    private Long id;

    /**
     * 删除状态：0-未删除；1-已删除
     */
    @Schema(title = "删除状态：0-未删除 1-已删除")
    @NotNull(message = "删除状态为空")
    private Integer isDeleted;


}
