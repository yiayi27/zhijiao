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
 * 会员卡权益规则表
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_card_privilege_rule")
@Schema(title = "MemberCardPrivilegeRule对象", description = "会员卡权益规则表")
public class MemberCardPrivilegeRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 特权计算类型 0-无需计算 1-折扣计算 2-倍数计算 3-指定价格计算 4-免除邮费计算
     */
    @Schema(title = "特权计算类型 0-无需计算 1-折扣计算 2-倍数计算 3-指定价格计算 4-免除邮费计算")
    @TableField("privilege_type")
    private Boolean privilegeType;

    /**
     * 计算值
     */
    @Schema(title = "计算值")
    @TableField("value")
    private BigDecimal value;

    /**
     * 权益id
     */
    @Schema(title = "权益id")
    @TableField("card_privilege_id")
    private Long cardPrivilegeId;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

}
