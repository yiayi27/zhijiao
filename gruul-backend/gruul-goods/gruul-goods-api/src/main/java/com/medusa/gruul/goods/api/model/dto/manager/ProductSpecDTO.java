package com.medusa.gruul.goods.api.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 商品规格参数
 *
 * @author 张治保
 * date 2024/11/12
 */
@Data
@Schema(title = "新增或修改商品规格DTO")
public class ProductSpecDTO {

    @Schema(title = "规格id")
    private Long id;

    @Schema(title = "父规格id")
    private Long parentId;

    @Schema(title = "规格名称")
    private String name;

    @Schema(title = "规格排序")
    private Integer order;

    @Schema(title = "子规格")
    private List<ProductSpecDTO> children;
}
