package com.medusa.gruul.logistics.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhaozheng
 */
@Data
@Schema(title = "批量发货")
public class LogisticsBatchDeliverDto {

    @Schema(title = "订单id")
    private Long orderId;

    @Schema(title = "物流公司编号")
    private String deliverCode;

    @Schema(title = "物流公司名称")
    private String deliverName;

    @Schema(title = "物流单号")
    private String logisticsCode;


}
