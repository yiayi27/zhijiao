package com.medusa.gruul.goods.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品展示分类二级分类vo
 * </p>
 *
 * @author lcysike
 * @since 2023-10-03
 */
@Data
@Schema(title = "ApiProductShowCategorySecondVo对象", description = "商品展示分类二级分类查询返回信息")
public class ApiProductShowCategorySecondVo implements Serializable {

    private Long id;

    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "上级分类的编号：0表示一级分类")
    private Long parentId;

    @Schema(title = "展示分类id")
    private Long showCategoryId;
}