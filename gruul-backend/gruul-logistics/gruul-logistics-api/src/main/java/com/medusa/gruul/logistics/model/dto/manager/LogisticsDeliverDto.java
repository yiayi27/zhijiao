package com.medusa.gruul.logistics.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author zhaozheng
 */
@Data
@Schema(title = "批量发货")
public class LogisticsDeliverDto {

    @Schema(title = "物流公司编号")
    private List<LogisticsBatchDeliverDto> logisticsBatchDeliverDtos;


}
