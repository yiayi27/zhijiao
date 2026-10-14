package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Member price vo.
 * <p>
 * 商品会员价
 *
 * @author alan
 * @date 2023/11/5 21:39
 */
@Data
@Schema(title = "ApiMemberPrice对象", description = "小程序商品会员价查询返回信息")
public class MemberPriceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 产品id
     */
    @Schema(title = "产品id", example = "1")
    private Long productId;

    /**
     * 产品sku-id
     */
    @Schema(title = "产品sku-id", example = "1")
    private Long skuId;

    /**
     * 会员等级id
     */
    @Schema(title = "会员等级id", example = "1")
    private Long memberLevelId;

    /**
     * 会员价格
     */
    @Schema(title = "会员价格")
    private BigDecimal memberPrice;

    /**
     * 会员等级名称
     */
    @Schema(title = "会员等级名称")
    private String memberLevelName;
}

