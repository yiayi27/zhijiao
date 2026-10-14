package com.medusa.gruul.order.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 发货单生成(发送)消息体
 *
 * @author alan
 * @date 2023/10/6 14:08
 */
@Data
@Schema(title = "发货单生成")
public class GenerateSendBillOrderMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 发货单ID
     */
    @Schema(title = "发货单ID,必填", example = "1")
    private Long id;
    /**
     * 发货单名称
     */
    @Schema(title = "发货单名称,必填", example = "发货单202001132022")
    private String name;
    /**
     * 发货单的状态 1->新增；2->发货;
     */
    @Schema(title = "发货单的状态 1->新增；2->发货;必填", example = "1")
    private Integer status;
    /**
     * 发货单的类型 1->直配发货单；2->物流发货单;
     */
    @Schema(title = "发货单的类型 1->直配发货单；2->物流发货单;必填", example = "1")
    private Integer type;
    /**
     * 发货单包含的ID
     */
    @Schema(title = "订单ID,必填")
    private List<Long> orderIds;
    /**
     * 物流公司 物流模式
     */
    @Schema(title = "物流公司,物流发货单必填", example = "顺丰快递")
    private String deliveryCompany;
    /**
     * 物流单号 物流模式
     */
    @Schema(title = "物流单号,物流发货单必填", example = "75324542769471")
    private String deliverySn;
    /**
     * 发货时间
     */
    @Schema(title = "发货时间,发货单状态为2必填", example = "2024-01-13 12:00:00")
    private String deliveryTime;

}
