package com.medusa.gruul.recycling.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 回收订单表
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_recycling_order")
@Schema(title = "RecyclingOrder对象", description = "回收订单表")
public class RecyclingOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(title = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 物流单号
     */
    @Schema(title = "物流单号")
    @TableField("shipment_number")
    private String shipmentNumber;
    /**
     * 物流公司
     */
    @Schema(title = "物流公司")
    @TableField("shipment_name")
    private String shipmentName;

    /**
     * 物品图片
     */
    @Schema(title = "物品图片")
    @TableField("article_imgs")
    private String articleImgs;

    /**
     * 凭证图片
     */
    @Schema(title = "凭证图片")
    @TableField("proof_imgs")
    private String proofImgs;

    /**
     * 寄出时间
     */
    @Schema(title = "寄出时间")
    @TableField("send_out")
    private LocalDateTime sendOut;

    /**
     * 回收状态 0-待寄出  1-已寄出  2-取消
     */
    @Schema(title = "回收状态 0-待寄出  1-已寄出  2-取消 ")
    @TableField("ed_status")
    private Integer edStatus;

    /**
     * 店铺用户id
     */
    @Schema(title = "店铺用户id")
    @TableField("shop_user_id")
    private String shopUserId;

    /**
     * 获得卡券id
     */
    @Schema(title = "获得卡券id")
    @TableField("coupon_ids")
    private String couponIds;

    /**
     * 用户名称
     */
    @Schema(title = "用户名称")
    @TableField("nick_name")
    private String nickName;

    /**
     * 头像url
     */
    @Schema(title = "头像url")
    @TableField("avatar_url")
    private String avatarUrl;

}
