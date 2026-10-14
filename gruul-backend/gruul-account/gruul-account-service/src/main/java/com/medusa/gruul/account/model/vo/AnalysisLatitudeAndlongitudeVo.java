package com.medusa.gruul.account.model.vo;

import com.medusa.gruul.common.dto.AreaDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2023/12/1
 */
@Data
public class AnalysisLatitudeAndlongitudeVo {

    @Schema(title = "省")
    AreaDto province;
    @Schema(title = "市")
    AreaDto city;
    @Schema(title = "区")
    AreaDto district;
    @Schema(title = "区")
    String adcode;
}
