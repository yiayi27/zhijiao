package com.medusa.gruul.integral.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/5/14
 */
@Data
public class ShopFunctionInfoVo {
    @Schema(title = "类型 :0积分商城关闭 1.积分商城开启")
    private Boolean shop;
    @Schema(title = "签到是否开启 ")
    private Boolean singIn;
    @Schema(title = "步数是否开启")
    private Boolean stepNumber;

}
