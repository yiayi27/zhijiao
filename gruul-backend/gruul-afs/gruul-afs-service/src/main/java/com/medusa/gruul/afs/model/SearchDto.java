package com.medusa.gruul.afs.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type Search dto.
 *
 * @author alan
 * @description: 商家售后订单列表查询参数
 * @date 2024/8/5 21:54
 */
@Data
@Schema(title = "商家售后订单列表查询参数")
public class SearchDto extends AbstractQueryParam {

    @Schema(title = "商品名称，模糊搜索")
    private String goodsName;

    @Schema(title = "买家昵称，模糊搜索")
    private String userName;

    @Schema(title = "收货人名称，模糊搜索")
    private String receiverName;

    @Schema(title = "订单编号，精确搜索")
    private String orderId;

    @Schema(title = "取货地点，模糊搜索")
    private String pointName;


    @Schema(title = "线路id", example = "1")
    private Long lineId;

    @NotNull
    @Schema(title = "配送方式 0->全部;100->用户自提;102->物流配送")
    private Integer deliverType;

    @Schema(title = "开始日期,格式2023-10-10")
    private String startTime;

    @Schema(title = "结束日期,格式2023-10-10")
    private String endTime;

    @NotNull
    @Schema(title = "订单状态 -1：所有订单, 0.待处理, 1.处理中, 2.已完成, 3.已关闭")
    private Integer status;

    @NotNull
    @Schema(title = "销售专区  1->社区；2->商超")
    private Integer area;
}
