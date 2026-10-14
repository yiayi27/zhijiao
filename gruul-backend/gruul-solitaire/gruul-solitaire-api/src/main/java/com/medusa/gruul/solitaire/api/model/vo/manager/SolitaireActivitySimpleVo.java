package com.medusa.gruul.solitaire.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 接龙活动返回数据
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "FullScaleSimpleVo", description = "活动查询返回信息")
public class SolitaireActivitySimpleVo {

    @Schema(title = "活动信息")
    private SolitaireActivityVo solitaireActivityVo;

    @Schema(title = "活动关联商品详细信息")
    private List<ProductShowVo> productVos;

}
