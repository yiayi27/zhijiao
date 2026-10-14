package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 属性模板
 * </p>
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@Schema(title = "AttributeTemplateVo", description = "属性模板查询返回信息")
public class AttributeTemplateVo {

    private Long id;

    @Schema(title = "模版id", example = "1")
    private Long parentId;

    @Schema(title = "模版名称")
    private String name;

    @Schema(title = "内容")
    private String content;

    @Schema(title = "属性列表")
    private List<AttributeTemplateSecondVo> attributeTemplates;
}