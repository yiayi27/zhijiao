package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * The type Manage search logistics order dto.
 * <p>
 * 快递订单后台订单查询条件
 * @author lcysike
 */
@Data
@Schema(title = "快递订单后台订单查询条件")
public class ManageSearchLogisticsOrderDto implements Serializable {
    private static final long serialVersionUID = -3263921252635611410L;

    @Schema(title = "订单ID列表")
    private String orderIds;


}
