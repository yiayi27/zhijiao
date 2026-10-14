package com.medusa.gruul.logistics.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lcy
 */
@Data
@Schema(title = "查询物流基础运送方式信息VO")
public class LogisticsShippingModelVo {

    @Schema(title = "唯一 id, 新增不传,修改传原值 ")
    private Long id;

    @Schema(title = "模板 id,新增的运费模板时候 不传")
    private Long logisticsId;

    @Schema(title = "计价方式: 1=按件数,2=按重量 ")
    private Integer valuationModel;

    @Schema(title = "首件数")
    private Integer firstPiece;

    @Schema(title = "首重 单位: 千克(kg) ")
    private BigDecimal firstWeight;

    @Schema(title = "首费 单位元 ")
    private BigDecimal firstAmount;

    @Schema(title = "续件数量 ")
    private Integer secondPiece;

    @Schema(title = "续重 单位 千克/kg ")
    private BigDecimal secondWeight;

    @Schema(title = "续费, 单位: 元 ")
    private BigDecimal secondAmount;

    @Schema(title = "运送地区 json 格式 {\"provinceid\": [\"cityId\",\"cityId2\"]} ")
    private String region;

}
