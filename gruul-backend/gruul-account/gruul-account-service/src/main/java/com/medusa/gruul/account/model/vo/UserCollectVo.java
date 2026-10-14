package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiaoq
 * @date 2024/2/24 10:57
 */

@Data
@Schema(title = "UserCollectVo对象", description = "用户收藏展示信息")
public class UserCollectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "用户收藏表id")
    private Long collectId;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "商品id")
    private Long productId;

    @Schema(title = "商品主图")
    private String productPic;

    @Schema(title = "商品名称")
    private String productName;

    @Schema(title = "商品状态  0-上架 1-下架 2-售罄")
    private Integer status;

    @Schema(title = "商品实际售价")
    private BigDecimal productPrice;

    @Schema(title = "指导价划线价")
    private BigDecimal originalPrice;


}
