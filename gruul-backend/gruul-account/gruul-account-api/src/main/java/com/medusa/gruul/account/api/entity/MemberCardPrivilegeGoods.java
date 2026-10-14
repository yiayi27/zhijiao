package com.medusa.gruul.account.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 会员卡商品权益与商品关联表
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_card_privilege_goods")
@Schema(title = "MemberCardPrivilegeGoods对象", description = "会员卡商品权益与商品关联表")
public class MemberCardPrivilegeGoods extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 计算方式 0-折扣 1-减价 2-指定价格
     */
    @Schema(title = "计算方式 0-折扣 1-减价 2-指定价格")
    @TableField("compute_type")
    private Boolean computeType;

    /**
     * 商品id
     */
    @Schema(title = "商品id")
    @TableField("goods_id")
    private Long goodsId;

    /**
     * 会员卡权益id
     */
    @Schema(title = "会员卡权益id")
    @TableField("card_privilege_id")
    private Long cardPrivilegeId;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

}
