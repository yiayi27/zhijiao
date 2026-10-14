package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.AttributeTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 属性模板
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@Schema(title = "新增或修改模板DTO")
public class AttributeTemplateDto {

    private Long id;

    @Schema(title = "上级id（一级为0）", example = "0")
    private Long parentId;

    @NotBlank
    @Size(max = 64)
    @Schema(title = "模版名称")
    private String name;

    @Schema(title = "内容")
    private String content;

    @Schema(title = "属性列表")
    private List<AttributeTemplateSecondDto> attributeTemplates;

    public AttributeTemplate coverAttributeTemplate() {
        AttributeTemplate attributeTemplate = new AttributeTemplate();
        BeanUtil.copyProperties(this, attributeTemplate);
        return attributeTemplate;
    }
}