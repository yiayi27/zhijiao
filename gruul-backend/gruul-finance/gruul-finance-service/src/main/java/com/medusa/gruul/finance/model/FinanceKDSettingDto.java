package com.medusa.gruul.finance.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Finance kd setting dto.
 *
 * @Description: FinanceKDSettingDto.java
 * @Author: alan
 * @Date: 2024/2/28 14:58
 */
@Data
@Schema(title = "快递设置")
public class FinanceKDSettingDto {

    /**
     * 快递AppId
     */
    @Schema(title = "快递AppId")
    private String kdAppId;

    /**
     * 快递AppKey
     */
    @Schema(title = "快递AppKey")
    private String kdAppKey;


}
