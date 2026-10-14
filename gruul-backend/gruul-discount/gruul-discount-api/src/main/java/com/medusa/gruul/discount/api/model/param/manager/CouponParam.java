package com.medusa.gruul.discount.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author lcysike
 * @Description: 优惠劵
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "CouponParam对象", description = "优惠劵查询参数")
public class CouponParam extends AbstractQueryParam {

    @Schema(title = "优惠劵名称")
    private String name;

    @Schema(title = "优惠劵状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "优惠劵类型：  0-无门槛折扣劵  1-无门槛现金劵  2-满折劵  3-满减劵")
    private Integer couponType;

    @Schema(title = "赠送类型：  0-全民活动  1-赠予客户")
    private Integer giveType;

    @Schema(title = "开始时间")
    private String startTime;

    @Schema(title = "结束时间")
    private String endTime;
}