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
@TableName("t_shops_menu_config")
@Schema(title = "店铺菜单属性配置实体", description = "店铺菜单属性配置表")
@Getter
@Setter
public class ShopsMenuConfig extends BaseEntity {


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
     * 组件属性 json串
     */
    @NotNull
    @Schema(title = "组件属性 json串")
    @TableField("properties")
    private String properties;

}