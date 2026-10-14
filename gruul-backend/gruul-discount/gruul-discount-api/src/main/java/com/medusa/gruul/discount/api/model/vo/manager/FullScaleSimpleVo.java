package com.medusa.gruul.discount.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 满减活动
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "FullScaleSimpleVo", description = "满减活动查询返回信息")
public class FullScaleSimpleVo {

    @Schema(title = "满减活动信息")
    private FullScaleVo fullScaleVo;

    @Schema(title = "满减活动关联商品详细信息")
    private List<DiscountProductShowVo> discountProductVos;

}
