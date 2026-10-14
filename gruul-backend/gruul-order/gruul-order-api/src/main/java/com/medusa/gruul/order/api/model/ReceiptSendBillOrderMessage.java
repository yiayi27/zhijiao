package com.medusa.gruul.order.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 发货单签收消息体
 *
 * @author alan
 * @date 2023/10/6 14:08
 */
@Data
public class ReceiptSendBillOrderMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 发货单ID
     */
    private Long id;
    /**
     * 发货单包含的ID
     */
    @Schema(title = "订单ID,必填")
    private List<Long> orderIds;

}
