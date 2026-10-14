package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品属性
 * </p>
 *
 * @author lcysike
 * @since 2023-10-03
 */
@Data
@Schema(title = "ProductAttribute对象", description = "商品属性查询返回信息")
public class ProductAttributeVo implements Serializable {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @Schema(title = "属性名称")
    private String name;

    @Schema(title = "属性值")
    private String value;
}