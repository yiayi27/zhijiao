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
 * 会员卡信息表
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_card_info")
@Schema(title = "MemberCardInfo对象", description = "会员卡信息表")
public class MemberCardInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员卡等级代码
     */
    @Schema(title = "会员卡等级代码")
    @TableField("rank_code")
    private String rankCode;

    /**
     * 会员卡名称
     */
    @Schema(title = "会员卡名称")
    @TableField("rank_name")
    private String rankName;

    /**
     * 会员卡图片
     */
    @Schema(title = "会员卡图片")
    @TableField("back_ground_pic_url")
    private String backGroundPicUrl;

    /**
     * 会员卡标题
     */
    @Schema(title = "会员卡标题")
    @TableField("title")
    private String title;

    /**
     * 开通所需值(付费金额或所需积分)
     */
    @Schema(title = "开通所需值(付费金额或所需积分)")
    @TableField("open_value")
    private BigDecimal openValue;

    /**
     * 会员卡介绍
     */
    @Schema(title = "会员卡介绍")
    @TableField("introduce")
    private String introduce;

    /**
     * 会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换
     */
    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换")
    @TableField("open_type")
    private Integer openType;

    /**
     * 使用期限 按天计算 0表示不限制
     */
    @Schema(title = "使用期限 按天计算  0表示不限制")
    @TableField("use_in_date")
    private Long useInDate;

    /**
     * 开卡条件1 累计消费满多少元 0元表示不限制
     */
    @Schema(title = "开卡条件1 累计消费满多少元  0元表示不限制")
    @TableField("consume_condition")
    private BigDecimal consumeCondition;

    /**
     * 开卡条件2 是否需要购买指定商品,0-不需要 1-需要(查询关联子表)
     */
    @Schema(title = "开卡条件2 是否需要购买指定商品,0-不需要 1-需要(查询关联子表)")
    @TableField("is_buy_goods")
    private Integer buyGoods;

    /**
     * 会员卡是否开启 0-关闭 1-开启
     */
    @Schema(title = "会员卡是否开启 0-关闭 1-开启")
    @TableField("is_open")
    private Integer isOpen;

    /**
     * 会员卡风格 1-系统风格 2-自定义风格
     */
    @Schema(title = "会员卡风格 1-系统风格 2-自定义风格")
    @TableField("style")
    private Integer style;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

}
