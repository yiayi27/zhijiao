package com.medusa.gruul.logistics.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lcy
 */
@Data
@Schema(title = "查询物流指定包邮运送方信息VO")
public class LogisticsIncludePostageVo {

    @Schema(title = "唯一 id, 新增不传,修改传原值 ")
    private Long id;

    @Schema(title = "模板 id,新增运费模板的时候可不传")
    private Long logisticsId;

    @Schema(title = "包邮条件类型: 0= 按件数包邮,1=按重量包邮,2=按金额包邮,3=件数+金额 4=重量+金额 包邮")
    private Integer type;

    @Schema(title = "包邮地区 格式: {\"provinceid\": [\"cityId\",\"cityId2\"]}")
    private String region;

    @Schema(title = "包邮 件数")
    private Integer pieceNum;

    @Schema(title = "包邮重量 单位: 千克(kg) ")
    private BigDecimal weight;

    @Schema(title = "包邮金额 单位元")
    private BigDecimal amountNum;

}
