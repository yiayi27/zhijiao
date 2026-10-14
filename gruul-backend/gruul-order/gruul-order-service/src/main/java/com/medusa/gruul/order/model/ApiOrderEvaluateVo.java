package com.medusa.gruul.order.model;

import com.medusa.gruul.order.api.entity.OrderProductEvaluate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Api order evaluate vo.
 * <p>
 * 订单评价列表
 *
 * @author alan
 * @date 2023/11/25 21:41
 */
@Data
public class ApiOrderEvaluateVo {

    @Schema(title = "订单评价ID", example = "1")
    private Long id;

    @Schema(title = "订单ID", example = "1")
    private Long orderId;

    @Schema(title = "评价时间")
    private LocalDateTime createTime;

    @Schema(title = "商品详情")
    private List<OrderProductEvaluate> itemList;


    @Deprecated
    @Schema(title = "商城模式下店铺评分")
    private Integer shopRate;
}
