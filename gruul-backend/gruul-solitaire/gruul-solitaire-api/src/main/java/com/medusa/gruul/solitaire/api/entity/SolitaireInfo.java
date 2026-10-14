package com.medusa.gruul.solitaire.api.entity;

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
 * 接龙信息设置表
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_solitaire_info")
@Schema(title = "SolitaireInfo对象", description = "接龙信息设置表")
public class SolitaireInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 本店店铺id
     */
    @Schema(title = "本店店铺id")
    @TableField("shop_id")
    private String shopId;

    /**
     * 是否开启接龙活动 0-关闭 1-开启
     */
    @Schema(title = "是否开启接龙活动 0-关闭 1-开启")
    @TableField("activity_state")
    private Integer activityState;

    /**
     * 新人欢迎内容
     */
    @Schema(title = "新人欢迎内容")
    @TableField("welcome_info")
    private String welcomeInfo;

    /**
     * 新人欢迎内容设置 0-关闭 1-开启
     */
    @Schema(title = "新人欢迎内容设置 0-关闭 1-开启")
    @TableField("welcome_info_set")
    private Integer welcomeInfoSet;

    /**
     * 接单提醒内容
     */
    @Schema(title = "接单提醒内容")
    @TableField("pay_order_info")
    private String payOrderInfo;

    /**
     * 接单提醒内容设置 0-关闭 1-开启
     */
    @Schema(title = "接单提醒内容设置 0-关闭 1-开启")
    @TableField("pay_order_info_set")
    private Integer payOrderInfoSet;

    /**
     * 到货提醒内容
     */
    @Schema(title = "到货提醒内容")
    @TableField("arrive_info")
    private String arriveInfo;

    /**
     * 到货提醒内容设置 0-关闭 1-开启
     */
    @Schema(title = "到货提醒内容设置 0-关闭 1-开启")
    @TableField("welcome_info_set")
    private Integer arriveInfoSet;

}
