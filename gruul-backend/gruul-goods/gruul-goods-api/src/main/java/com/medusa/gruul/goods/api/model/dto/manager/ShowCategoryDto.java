package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.ShowCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


/**
 * <p>
 * 展示分类
 * </p>
 *
 * @author lcysike
 * @since 2023-10-03
 */
@Data
@Schema(title = "新增或修改展示分类大类DTO")
public class ShowCategoryDto {

    private Long id;

    @Schema(title = "上机分类的编号：0表示一级分类")
    @NotNull
    private Long parentId;

    @Schema(title = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(title = "分类级别：0->1级；1->2级")
    @NotNull
    private Integer level;

    @Schema(title = "销售专区")
    private Long saleMode;

    @Schema(title = "分类排序")
    private Integer sort;

    @Schema(title = "下级分类")
    private List<ShowCategorySecondDto> showCategorySecondDtos;

    public ShowCategory coverShowCategory() {
        ShowCategory showCategory = new ShowCategory();
        BeanUtil.copyProperties(this, showCategory);
        return showCategory;
    }
}