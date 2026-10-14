package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/6/21
 */
@Data
public class LatitudeAndlongitudeVo {

    @Schema(title = "经纬度返回顺序为 经度，纬度")
    private String location;

    @Schema(title = "省市区")
    private AnalysisLatitudeAndlongitudeVo andlongitudeVo;
}
