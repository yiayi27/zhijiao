package com.medusa.gruul.discount.api.model.vo.api;

import com.medusa.gruul.discount.api.model.vo.manager.FullScaleRuleVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 小程序满减活动
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "ApiFullScaleVo", description = "小程序满减活动查询返回信息")
public class ApiFullScaleVo {

    private Long id;

    @Schema(title = "活动名称")
    private String name;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @Schema(title = "可用抵扣： 0-积分")
    private String deductionType;

    @Schema(title = "适用优惠：0-会员价  1-优惠劵 (多个用逗号隔开)")
    private String applyType;

    @Schema(title = "活动状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "满减活动规则信息")
    private List<FullScaleRuleVo> fullScaleRules;
}
