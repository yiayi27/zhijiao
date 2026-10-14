package com.medusa.gruul.shops.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("t_shops")
@Schema(title = "总店 实体", description = "总店表")
@Getter
@Setter
public class Shops extends BaseEntity {


    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 店铺id
     */
    @NotNull
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;


    /**
     * 协议
     */
    @NotNull
    @Schema(title = "协议")
    @TableField("agreement")
    private Long agreement;


    /**
     * 起提金额
     */
    @NotNull
    @Schema(title = "起提金额")
    @TableField("amount_raised")
    private Long amountRaised;


    /**
     * 逻辑删除标识  0正常 1已删除
     */
    @NotNull
    @Schema(title = "逻辑删除标识  0正常 1已删除")
    @TableField("is_deleted")
    private String isDeleted;

}