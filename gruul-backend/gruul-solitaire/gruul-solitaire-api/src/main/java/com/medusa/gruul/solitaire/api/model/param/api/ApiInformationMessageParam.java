package com.medusa.gruul.solitaire.api.model.param.api;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 小程序资讯留言查询参数
 * </p>
 *
 * @author lcysike
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ApiInformationMessageParam", description = "小程序资讯留言查询参数")
public class ApiInformationMessageParam extends AbstractQueryParam {

    /**
     * 资讯id
     */
    @Schema(title = "资讯id")
    private Long infoId;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

}
