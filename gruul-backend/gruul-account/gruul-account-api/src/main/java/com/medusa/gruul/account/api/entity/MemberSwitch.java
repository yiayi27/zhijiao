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
 * 会员相关开关
 * </p>
 *
 * @author zhaokw
 * @since 2024-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_switch")
@Schema(title = "MemberSwitch对象", description = "会员相关开关")
public class MemberSwitch extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 开关状态：0-关 1-开
     */
    @Schema(title = "开关状态：0-关 1-开")
    @TableField("state")
    private Integer state;

    /**
     * 会员模块名：0-总开关 1-储值开关
     */
    @Schema(title = "会员模块名：0-总开关 1-储值开关")
    @TableField("member_model")
    private Integer memberModel;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;


}
