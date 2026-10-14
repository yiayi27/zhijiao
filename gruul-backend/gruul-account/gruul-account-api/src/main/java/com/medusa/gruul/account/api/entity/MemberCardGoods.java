package com.medusa.gruul.account.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会卡购买指定商品关联表
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_card_goods")
@Schema(title = "MemberCardGoods对象", description = "会卡购买指定商品关联表")
public class MemberCardGoods extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    /**
     * 会员卡id
     */
    @Schema(title = "会员卡id")
    @TableField("member_card_id")
    private Long memberCardId;

    /**
     * 商品id
     */
    @Schema(title = "商品id")
    @TableField("goods_id")
    private String goodsId;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

}
