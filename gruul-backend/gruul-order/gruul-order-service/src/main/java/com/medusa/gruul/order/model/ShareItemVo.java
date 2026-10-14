package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Share item vo.
 * <p>
 * 晒单商品详情
 *
 * @author alan
 * @date 2024/7/8 21:28
 */
@Data
@Schema(title = "晒单商品详情")
public class ShareItemVo {

    /**
     * 商品图片
     */
    @Schema(title = "商品图片")
    private String productPic;

    /**
     * 商品名
     */
    @Schema(title = "商品名")
    private String productName;

    /**
     * 购买数量
     */
    @Schema(title = "购买数量")
    private Integer productQuantity;

}
