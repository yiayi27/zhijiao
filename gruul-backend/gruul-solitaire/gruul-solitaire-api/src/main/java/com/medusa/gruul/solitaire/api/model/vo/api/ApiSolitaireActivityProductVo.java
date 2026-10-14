package com.medusa.gruul.solitaire.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 微信端接龙活动商品查询返回信息
 * </p>
 *
 * @author lcysike
 * @since 2024-07-02
 */
@Data
@Schema(title = "SolitaireActivityProductVo", description = "微信端接龙活动商品查询返回信息")
public class ApiSolitaireActivityProductVo {

    private Long id;

    @Schema(title = "接龙活动id")
    private Long activityId;

    @Schema(title = "商品id")
    private Long productId;
}
