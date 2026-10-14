package com.medusa.gruul.afs.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type User apply dto.
 *
 * @author alan
 * @description: 用户申请售后的参数
 * @date 2024/8/5 21:54
 */
@Data
@Schema(title = "用户申请售后的参数")
public class UserApplyDto extends BaseApplyDto {

    /**
     * 申请售后的订单ID
     */
    @NotNull(message = "申请售后的订单不能为空")
    @Schema(title = "申请售后的订单ID")
    private Long orderId;

}
