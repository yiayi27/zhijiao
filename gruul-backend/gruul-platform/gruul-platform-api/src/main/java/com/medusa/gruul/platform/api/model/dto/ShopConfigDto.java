package com.medusa.gruul.platform.api.model.dto;

import com.medusa.gruul.platform.api.model.vo.PayInfoVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 店铺相关配置
 *
 * @author whh
 * @description
 * @date: 2024/6/13
 */
@Data
public class ShopConfigDto {

    @Schema(title = "支付相关配置")
    private PayInfoVo payInfo;

}
