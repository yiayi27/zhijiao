package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单评论表
 * </p>
 *
 * @author alan
 * @since 2023-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_evaluate")
@Schema(title = "OrderEvaluate对象", description = "订单评论表")
public class OrderEvaluate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 用户帐号
     */
    @Schema(title = "用户帐号")
    @TableField("user_name")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    @TableField("user_avatar_url")
    private String userAvatarUrl;

    /**
     * 订单id
     */
    @Schema(title = "订单id")
    @TableField("order_id")
    private Long orderId;


    /**
     * 商城模式下店铺评分
     */
    @Schema(title = "商城模式下店铺评分")
    @TableField("shop_rate")
    private Integer shopRate;


}
