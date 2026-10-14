package com.medusa.gruul.solitaire.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 资讯信息查询参数
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "InformationInfoParam对象", description = "资讯信息查询参数")
public class InformationInfoParam extends AbstractQueryParam {

    private Long id;

    @Schema(title = "资讯标题")
    private String title;

    @Schema(title = "链接名称")
    private String urlName;
}
