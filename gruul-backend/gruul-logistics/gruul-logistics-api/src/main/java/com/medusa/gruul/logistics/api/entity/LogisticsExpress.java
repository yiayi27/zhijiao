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
 * 物流公司发货地址设置
 * t_logistics_express
 *
 * @author
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_logistics_express")
@Schema(title = "LogisticsExpress对象", description = "物流公司发货地址设置")
public class LogisticsExpress extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.AUTO)

    private Long id;

    /**
     * 快递公司名称
     */
    @NotNull
    @Schema(title = "快递公司名称")
    @TableField("name")
    private String name;

    /**
     * 快递公司code
     */
    @NotNull
    @Schema(title = "快递公司code")
    @TableField("code")
    private String code;

    /**
     * 快递公司客服电话
     */
    @NotNull
    @Schema(title = "快递公司客服电话")
    @TableField("phone")
    private String phone;

    /**
     * 地址id 关联物流发货地址表id
     */
    @NotNull
    @Schema(title = "地址id 关联物流发货地址表id")
    @TableField("address_id")
    private Long addressId;

    /**
     * 客户id
     */
    @NotNull
    @Schema(title = "客户id")
    @TableField("customer_name")
    private String customerName;

    /**
     * 客户密匙
     */
    @NotNull
    @Schema(title = "客户密匙")
    @TableField("customer_password")
    private String customerPassword;

    /**
     * 负责人
     */
    @NotNull
    @Schema(title = "负责人")
    @TableField("link_name")
    private String linkName;

    /**
     * 负责人电话
     */
    @NotNull
    @Schema(title = "负责人电话")
    @TableField("link_tel")
    private String linkTel;

    /**
     * 状态
     */
    @NotNull
    @Schema(title = "状态")
    @TableField("status")
    private String status;
}