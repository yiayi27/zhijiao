package com.medusa.gruul.logistics.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 物流公司与发货信息查询 物流公司设置表与退发货地址管理表联合查询对象
 *
 * @author lcysike
 */
@Data
@Schema(title = "物流公司与发货信息查询")
public class LogisticsExpressAddressVo {

    @Schema(title = "expressId")
    private Long expressId;

    @Schema(title = "快递公司名称")
    private String expressName;

    @Schema(title = "快递公司code")
    private String expressCode;

    @Schema(title = "发货地址id 关联物流发货地址表id")
    private Long addressId;

    @Schema(title = "客户id")
    private String customerName;

    @Schema(title = "客户密匙")
    private String customerPassword;

    @Schema(title = "发货人所在省")
    private String province;

    @Schema(title = "发货人所在市")
    private String city;

    @Schema(title = "发货人所在县")
    private String country;

    @Schema(title = "发货人地址")
    private String address;

    @Schema(title = "发货人名称")
    private String receiveName;

    @Schema(title = "发货人电话")
    private String receivePhone;

    @Schema(title = "发货人邮编")
    private String zipCode;

}