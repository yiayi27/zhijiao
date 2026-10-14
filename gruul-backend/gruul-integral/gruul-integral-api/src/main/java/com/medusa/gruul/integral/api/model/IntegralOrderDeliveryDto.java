package com.medusa.gruul.integral.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: 积分订单发货 (打印)
 * @Author: xiaoq
 * @Date: 2024/6/1 14:46
 */
@Data
@Schema(title = "物流发货")
public class IntegralOrderDeliveryDto {
    @NotNull
    @Schema(title = "订单id")
    private Long integralOrderId;

    @Schema(title = "物流公司")
    private String deliveryCompany;

    @Schema(title = "物流单号")
    private String deliverySn;

    @Schema(title = "集包地信息")
    private String packageName;

    @Schema(title = "集包地信息")
    private String packageCode;

    @Schema(title = "分拣码")
    private String sortingCode;

    @Schema(title = "物流公司编号")
    private String deliveryCode;
    //======入库参数==

    @Schema(title = "更新时间")
    private LocalDateTime updateTime;

    @Schema(title = "发货时间")
    private LocalDateTime deliveryTime;

    /**
     * 打印任务id
     */
    private String taskId;

}
