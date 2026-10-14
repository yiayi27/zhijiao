package com.medusa.gruul.discount.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author lcysike
 * @Description: 满减活动
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "FullScaleParam对象", description = "满减活动查询参数")
public class FullScaleParam extends AbstractQueryParam {

    @Schema(title = "满减活动名称")
    private String name;

    @Schema(title = "满减活动状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "开始时间")
    private String startTime;

    @Schema(title = "结束时间")
    private String endTime;
}