package com.medusa.gruul.order.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * The type Manage search order dto.
 * <p>
 * 管理后台订单查询条件
 *
 * @author alan
 * @date 2023/11/10 9:21
 */
@Data
@Schema(title = "管理后台订单查询条件")
public class ManageSearchOrderDto extends AbstractQueryParam {


    @Schema(title = "商品名称，模糊搜索")
    private String goodsName;


    @Schema(title = "买家昵称，模糊搜索")
    private String userName;


    @Schema(title = "收货人名称，模糊搜索")
    private String receiverName;


    @Schema(title = "订单编号，精确搜索")
    private String orderId;

    @Schema(title = "物流单号")
    private String deliverySn;


    @Schema(title = "线路id", example = "1")
    private Long lineId;


    @Schema(title = "备注状态 0->全部;1->有备注；2->无备注")
    private Integer remarkType;


    @Schema(title = "配送方式 0->全部;100->用户自提;102->物流配送")
    private Integer deliverType;


    @Deprecated
    @Schema(title = "成交日期,格式2023-10-10")
    private String payTime;


    @Schema(title = "开始日期,格式2023-10-10")
    private String startTime;


    @Schema(title = "结束日期,格式2023-10-10")
    private String endTime;

    @NotNull
    @Schema(title = "用于进行时间快速筛选，默认为近一个月:近一个月->0; 近三个月->1; 全部->2; ")
    private Integer quicklyDate;


    @Schema(title = "发货单id，-1:未生成发货单的订单，0:所有已生成发货单的订单，其他:根据发货单ID查询")
    private Long sendBillId;

    @NotNull
    @Schema(title = "订单状态 -1：所有订单, 0.待付款（待买家付款）, 1.待发货（买家已付款）, 2.配送中（卖家已发货）, 3.待提货（物流订单已发货）, 4" +
            ".已完成（用户已经签收）, 5.已关闭")
    private Integer orderStatus;

    @Schema(title = "销售专区  0->全部;1->社区；2->商超")
    private Integer area;

}
