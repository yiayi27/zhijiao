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
 * 会员卡权益表
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_card_privilege")
@Schema(title = "MemberCardPrivilege对象", description = "会员卡权益表")
public class MemberCardPrivilege extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权益图标
     */
    @Schema(title = "权益图标")
    @TableField("icon")
    private String icon;

    /**
     * 权益名称
     */
    @Schema(title = "权益名称")
    @TableField("privilege_name")
    private String privilegeName;

    /**
     * 权益说明
     */
    @Schema(title = "权益说明")
    @TableField("illustrate")
    private String illustrate;

    /**
     * 特权类型 0-自定义权益 1-商品权益 2-包邮权益 3-积分倍数权益 4-专属客服权益 5-优先发货 6-急速售后
     */
    @Schema(title = "特权类型 0-自定义权益 1-商品权益 2-包邮权益 3-积分倍数权益 4-专属客服权益 5-优先发货 6-急速售后")
    @TableField("p_type")
    private String pType;

    /**
     * 是否允许删除 0-不允许 1-允许
     */
    @Schema(title = "是否允许删除 0-不允许 1-允许")
    @TableField("is_allow_del")
    private Integer allowDel;

    /**
     * 特权开关 0-关闭 1-开启
     */
    @Schema(title = "特权开关 0-关闭  1-开启")
    @TableField("is_open")
    private Integer open;

    /**
     * 是否有多个权益值选择 0-否 1-是
     */
    @Schema(title = "是否有多个权益值选择 0-否 1-是")
    @TableField("is_rule")
    private String isRule;

    /**
     * 默认值(可以是折扣值或者其他值,目前只有p_type=1时代表未添加为权益商品的商品的默认值)
     */
    @Schema(title = "默认值(可以是折扣值或者其他值,目前只有p_type=1时代表未添加为权益商品的商品的默认值)")
    @TableField("def_value")
    private BigDecimal defValue;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

}
