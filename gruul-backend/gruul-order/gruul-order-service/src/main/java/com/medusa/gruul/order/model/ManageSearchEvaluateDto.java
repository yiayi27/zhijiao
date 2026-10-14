package com.medusa.gruul.order.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * The type Manage search evaluate dto.
 * <p>
 * 管理后台评价查询条件
 *
 * @author alan
 * @date 2023/11/10 9:21
 */
@Data
@Schema(title = "管理后台评价查询条件")
public class ManageSearchEvaluateDto extends AbstractQueryParam {


    @Schema(title = "商品名称，模糊搜索")
    private String goodsName;


    @Schema(title = "订单编号，精确搜索")
    private String orderId;


    @Schema(title = "成交日期开始,格式2023-10-10")
    private String payTimeStart;


    @Schema(title = "成交日期结束,格式2023-10-10")
    private String payTimeEnd;


    @Schema(title = "评价星级")
    private Integer rate;


}
