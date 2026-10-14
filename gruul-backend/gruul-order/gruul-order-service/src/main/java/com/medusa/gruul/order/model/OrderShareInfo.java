package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * The type Order share info.
 * <p>
 * 订单晒单字段
 *
 * @author alan
 * @date 2024/7/8 21:24
 */
@Data
@Schema(title = "订单晒单字段")
public class OrderShareInfo {
    /**
     * 标题
     */
    @Schema(title = "标题")
    private String title;

    /**
     * 背景图
     */
    @Schema(title = "背景图")
    private String background;
    /**
     * 商品详情集合
     */
    @Schema(title = "商品详情集合")
    private List<ShareItemVo> itemList;
}
