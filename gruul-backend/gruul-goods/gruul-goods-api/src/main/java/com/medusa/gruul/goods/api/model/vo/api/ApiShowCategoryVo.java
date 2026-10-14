package com.medusa.gruul.goods.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品展示分类一级分类VO
 *
 * @author kyl
 * @since 2023-11-05
 */
@Data
@Schema(title = "ApiShowCategoryVo对象", description = "小程序商品展示分类一级分类")
public class ApiShowCategoryVo {
    private Long id;

    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "下级分类")
    private List<ApiShowCategorySecondVo> showCategoryVos;
}