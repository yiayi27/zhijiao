package com.medusa.gruul.solitaire.api.model.param.api;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 小程序资讯信息查询参数
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ApiInformationInfoParam对象", description = "小程序资讯信息查询参数")
public class ApiInformationInfoParam extends AbstractQueryParam {

    /**
     * 专题id
     */
    @Schema(title = "专题id")
    private Long topicId;

}
