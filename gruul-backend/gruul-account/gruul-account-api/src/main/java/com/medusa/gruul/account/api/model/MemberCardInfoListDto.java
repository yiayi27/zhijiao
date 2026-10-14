package com.medusa.gruul.account.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description 获取累计消费满多少与购买指定商品信息
 * @Author zhaokw
 * @Date 9:32 2024\7\12 0012
 **/
@Data
@Schema(title = "获取累计消费满多少与购买指定商品信息")
@Accessors(chain = true)
public class MemberCardInfoListDto {

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    private String shopId;

    /**
     * 会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换
     */
    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换")
    private Integer openType;

}
