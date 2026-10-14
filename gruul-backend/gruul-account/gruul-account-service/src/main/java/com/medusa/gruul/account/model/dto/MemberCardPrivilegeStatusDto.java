package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 会员卡权益关闭启用请求参数
 *
 * @author zhaokw
 */
@Data
@Accessors(chain = true)
public class MemberCardPrivilegeStatusDto {

    @Schema(title = "会员权益id")
    @NotNull(message = "会员权益id为空")
    private Long id;

    /**
     * 特权开关 0-关闭 1-开启
     */
    @Schema(title = "特权开关 0-关闭  1-开启")
    @NotNull(message = "特权开关为空")
    private Integer open;


}
