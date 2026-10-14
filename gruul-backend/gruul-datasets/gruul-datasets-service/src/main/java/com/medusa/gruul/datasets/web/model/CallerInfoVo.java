package com.medusa.gruul.datasets.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 访客数据信息
 * @Author: xiaoq
 * @Date: 2024/5/8 9:11
 */
@Data
@Schema(title = "商铺访客信息数据")
public class CallerInfoVo {

    @Schema(title = "今日(浏览量)")
    private Integer pv;

    @Schema(title = "今日(访客人数)")
    private Integer uv;

    @Schema(title = "全部(访客人数)(总访客)")
    private Integer totalUv;

}
