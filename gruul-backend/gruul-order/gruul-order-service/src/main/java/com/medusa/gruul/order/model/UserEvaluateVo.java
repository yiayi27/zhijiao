package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type User evaluate vo.
 * <p>
 * 商品详情页用户评价
 *
 * @author alan
 * @date 2024/1/18 21:16
 */
@Data
@Schema(title = "商品详情页用户评价")
public class UserEvaluateVo {

    @Schema(title = "总条数")
    private int total;

    @Schema(title = "所有的")
    private int all;

    @Schema(title = "有内容的")
    private int hasContent;

    @Schema(title = "有图片的")
    private int hasPicture;

    @Schema(title = "好评率：99 表示好评率为99%")
    private int praiseRate;


}
