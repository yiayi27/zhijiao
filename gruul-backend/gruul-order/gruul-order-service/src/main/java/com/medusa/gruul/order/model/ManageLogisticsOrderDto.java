package com.medusa.gruul.order.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * The type Manage logistics order dto.
 * <p>
 * 快递订单后台订单查询条件
 * @author alan
 */
@Data
@Schema(title = "快递订单后台订单查询条件")
public class ManageLogisticsOrderDto extends AbstractQueryParam {


    @Schema(title = "商品名称，模糊搜索")
    private String goodsName;

    @Schema(title = "订单编号，精确搜索")
    private String orderId;

    @Schema(title = "取货地点，模糊搜索")
    private String address;

    @Schema(title = "买家昵称，模糊搜索")
    private String userName;

    @Schema(title = "取货地点，模糊搜索")
    private String receiverName;

}
