package com.medusa.gruul.logistics.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaozheng
 */
@Data
@NoArgsConstructor
@Schema(title = "查询收发货地址")
public class LogisticsAddressVo {

    @Schema(title = "主键 id,新增不传,修改传原值")
    private Long id;

    @Schema(title = "联系人名称")
    private String name;

    @Schema(title = "省")
    private String province;

    @Schema(title = "省id")
    private String provinceId;

    @Schema(title = "市")
    private String city;

    @Schema(title = "市id")
    private String cityId;

    @Schema(title = "县")
    private String country;

    @Schema(title = "县id")
    private String countryId;

    @Schema(title = "地址")
    private String address;

    @Schema(title = "联系电话")
    private String phone;

    @Schema(title = "邮编")
    private String zipCode;

    @Schema(title = "是否为默认发货地址: 0=不是,1=是")
    private Integer defSend;

    @Schema(title = "是否为默认退货地址: 0=不是,1=是")
    private Integer defReceive;

    @Schema(title = "纬度")
    private String lat;

    @Schema(title = "经度")
    private String lng;

}
