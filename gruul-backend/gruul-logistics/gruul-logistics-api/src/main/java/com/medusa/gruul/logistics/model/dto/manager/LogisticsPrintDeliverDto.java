package com.medusa.gruul.logistics.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author zhaozheng
 */
@Data
@Schema(title = "打印发货")
public class LogisticsPrintDeliverDto {

    @Schema(title = "订单id")
    private List<Long> orderIds;

    @Schema(title = "店铺id")
    private String shopId;

    @Schema(title = "物流公司编号")
    @NotBlank(message = "物流公司不能为空")
    private String deliverCode;

    @Schema(title = "打印机编号")
    @NotBlank(message = "打印机编号不能为空")
    private String printCode;

    @Schema(title = "物流公司发货地址设置id")
    @NotNull(message = "物流公司发货地址不能为空")
    private Long expressId;

    @Schema(title = "是否为积分订单 为-1的时候为积分订单")
    private Integer isIntegralOrder;

    @Schema(title = "打印机信息id")
    @NotBlank(message = "打印机信息id不能为空")
    private String logisticsExpressPrintId;

}
