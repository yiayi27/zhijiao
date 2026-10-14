package com.medusa.gruul.order.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(title = "查询对象")
public class GetOrderListDtoByTimeScope {
    @Schema(title = "开始时间", format = "yyyy-MM-dd HH:mm:ss")
    private String start;
    @Schema(title = "结束时间 ", format = "yyyy-MM-dd HH:mm:ss")
    private String end;
    @Schema(title = "商铺 id ")
    private String shopId;

    public GetOrderListDtoByTimeScope(String start, String end, String shopId) {
        this.start = start;
        this.end = end;
        this.shopId = shopId;
    }
}