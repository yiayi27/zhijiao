package com.medusa.gruul.datasets.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 访客信息dto
 * @Author: xiaoq
 * @Date: 2024/5/7 14:25
 */
@Data
public class CallerDto {

    @Schema(title = "(浏览量)")
    private Integer pv;

    @Schema(title = "昨日(浏览量)")
    private Integer yesterdayPv;
}
