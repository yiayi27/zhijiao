package com.medusa.gruul.logistics.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author lcy
 */
@Data
@Schema(title = "查询运费模版信息VO")
public class LogisticsTemplateVo {

    @Schema(title = "主键 id, 新增 不传,修改传原值 ")
    private Long id;

    @Schema(title = "模板名称")
    private String name;

    @Schema(title = "计价方式: 1=按件数,2=按重量 ")
    private Integer valuationModel;

    @Schema(title = "是否指定包邮条件: 0=未指定,1=已指定 ")
    private Integer choiceConditionPostage;

    @Schema(title = "基础运送方式信息")
    private List<LogisticsShippingModelVo> logisticsShippingModelVos;

    @Schema(title = "指定包邮运送方式信息")
    private List<LogisticsIncludePostageVo> logisticsIncludePostageVos;

}
