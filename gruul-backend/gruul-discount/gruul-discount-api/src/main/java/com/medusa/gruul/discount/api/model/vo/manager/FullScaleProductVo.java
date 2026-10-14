package com.medusa.gruul.discount.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 满减活动适用商品
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "FullScaleProductVo", description = "满减活动适用商品查询返回信息")
public class FullScaleProductVo {

    private Long id;

    @Schema(title = "满减活动id")
    private Long fullScaleId;

    @Schema(title = "商品id")
    private Long productId;


}
