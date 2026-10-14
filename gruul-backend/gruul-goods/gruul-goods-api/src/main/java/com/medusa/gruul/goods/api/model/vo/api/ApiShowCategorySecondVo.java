package com.medusa.gruul.goods.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品展示分类二级分类VO
 *
 * @author kyl
 * @since 2023-11-05
 */
@Data
@Schema(title = "ApiShowCategorySecondVo对象", description = "小程序商品展示分类二级分类")
public class ApiShowCategorySecondVo {
    private Long id;

    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "商品数量")
    private String productNumber;
}