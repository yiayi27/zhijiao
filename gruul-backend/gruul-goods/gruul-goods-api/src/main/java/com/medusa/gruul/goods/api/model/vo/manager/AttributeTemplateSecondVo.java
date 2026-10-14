package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 属性模板子属性
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@Schema(title = "AttributeTemplateSecondVo", description = "属性模板查子属性询返回信息")
public class AttributeTemplateSecondVo {
    private Long id;

    @Schema(title = "模版id", example = "1")
    private Long parentId;

    @Schema(title = "模版名称")
    private String name;

    @Schema(title = "内容")
    private String content;
}