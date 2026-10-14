package com.medusa.gruul.goods.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 商品展示分类大类Vo
 * </p>
 *
 * @author lcysike
 * @since 2023-10-03
 */
@Data
@Schema(title = "ProductShowCategoryVo对象", description = "商品展示分类大类查询返回信息")
public class ApiProductShowCategoryVo implements Serializable {

    private Long id;

    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "上级分类的编号：0表示一级分类")
    private Long parentId;

    @Schema(title = "展示分类id")
    private Long showCategoryId;

    @Schema(title = "下级分类")
    private List<ApiProductShowCategorySecondVo> productShowCategorySeconds;
}