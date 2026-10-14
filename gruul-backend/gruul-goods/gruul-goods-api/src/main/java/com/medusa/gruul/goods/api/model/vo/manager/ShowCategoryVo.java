package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * <p>
 * 展示分类大类Vo
 * </p>
 *
 * @author lcysike
 * @since 2023-10-03
 */
@Data
@Schema(title = "ShowCategoryVo对象", description = "展示分类大类查询返回信息")
public class ShowCategoryVo {

    private Long id;

    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "上机分类的编号：0表示一级分类")
    private Long parentId;

    @Schema(title = "展示分类id(数据传输用)")
    private Long showCategoryId;

    @Schema(title = "销售专区")
    private Long saleMode;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "下级分类")
    private List<ShowCategorySecondVo> showCategoryVos;
}