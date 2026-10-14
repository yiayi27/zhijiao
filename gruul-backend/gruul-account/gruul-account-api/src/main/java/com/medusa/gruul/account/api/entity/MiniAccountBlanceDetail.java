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
 * 用户余额明细总表
 * </p>
 *
 * @author whh
 * @since 2024-07-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_mini_account_blance_detail")
@Schema(title = "MiniAccountBlanceDetail对象", description = "用户余额明细总表")
public class MiniAccountBlanceDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(title = "id ")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 商铺id
     */
    @Schema(title = "商铺id")
    @TableField("shop_id")
    private String shopId;

    /**
     * 店铺用户id
     */
    @Schema(title = "店铺用户id")
    @TableField("shop_user_id")
    private String shopUserId;

    /**
     * 操作金额
     */
    @Schema(title = "操作金额")
    @TableField("option_value")
    private BigDecimal optionValue;

    /**
     * 操作类型  1增加  2减少
     */
    @Schema(title = "操作类型  1增加  2减少")
    @TableField("option_type")
    private Integer optionType;

    /**
     * 操作源头  1积分消费  2-订单完成   3-会员开卡
     */
    @Schema(title = "操作源头  1积分消费  2-订单完成  ")
    @TableField("option_source")
    private Integer optionSource;

    /**
     * 业务数据id(根据自身业务传入订单id或有唯一代表性的id)
     */
    @Schema(title = "业务数据id(根据自身业务传入订单id或有唯一代表性的id) 数据字段长度32")
    @TableField("business_id")
    private String businessId;

}
