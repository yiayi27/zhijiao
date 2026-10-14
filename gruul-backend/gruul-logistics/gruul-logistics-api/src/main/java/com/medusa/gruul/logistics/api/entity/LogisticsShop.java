package com.medusa.gruul.logistics.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 店铺物流公司绑定
 * t_logistics_shop
 *
 * @author
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_logistics_shop")
@Schema(title = "LogisticsShop对象", description = "店铺物流公司绑定")
public class LogisticsShop extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
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
     * 物流公司id
     */
    @NotNull
    @Schema(title = "物流公司id")
    @TableField("logistics_company_id")
    private Long logisticsCompanyId;


    @Schema(title = "是否为默认物流公司: 0=不是,1=是")
    @TableField("is_default")
    private Integer isDefault;

}