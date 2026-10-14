package com.medusa.gruul.solitaire.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 资讯留言查询参数
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "InformationMessageParam对象", description = "资讯留言查询参数")
public class InformationMessageParam extends AbstractQueryParam {

    /**
     * 资讯id
     */
    @Schema(title = "资讯id")
    private Long infoId;

    /**
     * 用户留言内容
     */
    @Schema(title = "用户留言内容")
    private String userContent;

    /**
     * 用户名称
     */
    @Schema(title = "用户名称")
    private String nickName;

}
