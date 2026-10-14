package com.medusa.gruul.goods.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品属性信息
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@Schema(title = "ApiProductAttribute对象", description = "小程序商品属性查询返回信息")
public class ApiProductAttributeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 产品id
     */
    @Schema(title = "产品id")
    private Long productId;

    /**
     * 属性名称
     */
    @Schema(title = "属性名称")
    private String name;

    /**
     * 属性值
     */
    @Schema(title = "属性值")
    private String value;
}