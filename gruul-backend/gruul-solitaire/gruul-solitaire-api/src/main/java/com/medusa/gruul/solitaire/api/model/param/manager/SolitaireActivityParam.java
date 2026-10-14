package com.medusa.gruul.solitaire.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 接龙活动查询参数
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "SolitaireActivityParam对象", description = "接龙活动查询参数")
public class SolitaireActivityParam extends AbstractQueryParam {

    private Long id;

    @Schema(title = "活动名称")
    private String name;

    @Schema(title = "开始时间")
    private String startTime;

    @Schema(title = "结束时间")
    private String endTime;

    @Schema(title = "活动状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

}
