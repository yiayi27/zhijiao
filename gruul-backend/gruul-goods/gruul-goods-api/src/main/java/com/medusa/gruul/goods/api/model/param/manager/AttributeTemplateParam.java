package com.medusa.gruul.goods.api.model.param.manager;


import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 属性模板查询参数
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "AttributeTemplateParam对象", description = "属性模板查询参数")
public class AttributeTemplateParam extends AbstractQueryParam {
    private static final long serialVersionUID = 1;
}