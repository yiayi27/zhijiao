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
 * 会员卡信息表
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_card_base_info")
@Schema(title = "会员卡基础信息", description = "会员卡基础信息")
public class MemberCardBaseInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员卡标题
     */
    @Schema(title = "会员卡标题")
    private String title;


    /**
     * 会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换
     */
    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换 ")
    @TableField("current_open_type")
    private Integer currentOpenType;

    /**
     * 使用期限 按天计算 0表示不限制
     */
    @Schema(title = "使用期限 按天计算  0表示不限制")
    @TableField("use_in_date")
    private Long useInDate;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;


}
