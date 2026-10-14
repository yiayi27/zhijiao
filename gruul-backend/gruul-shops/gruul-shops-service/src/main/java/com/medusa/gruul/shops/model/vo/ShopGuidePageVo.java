package com.medusa.gruul.shops.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 店铺引导页vo
 * @Author: xiaoq
 * @Date: 2024/10/15 16:24
 */
@Data
@Schema(title = "ShopGuidePageVo 实体", description = "店铺引导页 vo")
public class ShopGuidePageVo {

    /**
     * id
     */
    private Long id;

    @Schema(title = "网址")
    private String url;

    @Schema(title = "路径")
    private String path;

    @Schema(title = "是否为默认 0为默认 1为自定义")
    private Integer defaulted;

    @Schema(title = "店铺id")
    private String shopId;

}
