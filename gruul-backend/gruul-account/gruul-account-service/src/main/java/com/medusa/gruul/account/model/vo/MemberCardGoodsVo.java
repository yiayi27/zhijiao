package com.medusa.gruul.account.model.vo;

import com.medusa.gruul.goods.api.model.vo.manager.DiscountProductVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description 会员购买指定商品集合
 * @Author zhaokw
 * @Date 21:19 2024\5\15 0015
 **/
@Data
@Schema(title = "会员购买指定商品集合")
@Accessors(chain = true)
public class MemberCardGoodsVo {

    @Schema(title = "会员购买指定商品id")
    private Long id;

    @Schema(title = "会员卡id")
    private Long memberCardId;

    @Schema(title = "商品id集合，商品id之间用逗号分隔")
    private String goodsIds;

    @Schema(title = "商品信息")
    private List<DiscountProductVo> discountProductVos;
}
