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

import java.math.BigDecimal;

/**
 * <p>
 * 会员卡与会员卡权益关联表
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_card_privilege_group")
@Schema(title = "MemberCardPrivilegeGroup对象", description = "会员卡与会员卡权益关联表")
public class MemberCardPrivilegeGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权益表id
     */
    @Schema(title = "权益表id")
    @TableField("privilege_id")
    private Long privilegeId;

    /**
     * 会员卡id
     */
    @Schema(title = "会员卡id")
    @TableField("member_card_id")
    private Long memberCardId;

    /**
     * 计算值
     */
    @Schema(title = "计算值")
    @TableField("value")
    private BigDecimal value;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;
}
