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
 * 收发货地址管理
 * t_logistics_address
 *
 * @author
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_logistics_address")
@Schema(title = "LogisticsAddress对象", description = "收发货地址管理")
public class LogisticsAddress extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.AUTO)

    private Long id;

    /**
     * 发货人-名称
     */
    @NotNull
    @Schema(title = "发货人-名称")
    @TableField("name")
    private String name;

    /**
     * 省
     */
    @NotNull
    @Schema(title = "省")
    @TableField("province")
    private String province;

    /**
     * 省id
     */
    @NotNull
    @Schema(title = "省id")
    @TableField("province_id")
    private String provinceId;

    /**
     * 市
     */
    @NotNull
    @Schema(title = "市")
    @TableField("city")
    private String city;

    /**
     * 市id
     */
    @NotNull
    @Schema(title = "市id")
    @TableField("city_id")
    private String cityId;

    /**
     * 县
     */
    @NotNull
    @Schema(title = "县")
    @TableField("country")
    private String country;

    /**
     * 县id
     */
    @NotNull
    @Schema(title = "县id")
    @TableField("country_id")
    private String countryId;

    /**
     * 发货地址
     */
    @NotNull
    @Schema(title = "发货地址")
    @TableField("address")
    private String address;

    /**
     * 邮编
     */
    @NotNull
    @Schema(title = "邮编号")
    @TableField("zip_code")
    private String zipCode;

    /**
     * 联系电话
     */
    @NotNull
    @Schema(title = "邮编号")
    @TableField("phone")
    private String phone;

    /**
     * 是否为默认发货地址: 0=不是,1=是
     */
    @Schema(title = "是否为默认发货地址: 0=不是,1=是")
    @TableField("def_send")
    private Integer defSend;

    /**
     * 是否为默认退货地址: 0=不是,1=是
     */
    @Schema(title = "是否为默认退货地址: 0=不是,1=是")
    @TableField("def_receive")
    private Integer defReceive;

    /**
     * 纬度
     */
    @Schema(title = "纬度")
    @TableField("lat")
    private String lat;

    /**
     * 经度
     */
    @Schema(title = "经度")
    @TableField("lng")
    private String lng;
}