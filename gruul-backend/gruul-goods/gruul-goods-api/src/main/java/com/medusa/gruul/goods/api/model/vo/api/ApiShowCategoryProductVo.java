package com.medusa.gruul.goods.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品展示分类下商品列表
 *
 * @author lcy
 * @since 2024-09-14
 */
@Data
@Schema(title = "ApiShowCategoryProductVo对象", description = "小程序展示分类下商品列表")
public class ApiShowCategoryProductVo {
    private Long id;

    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "下级分类")
    private List<ApiAliveProductVo> apiAliveProductVos;
}