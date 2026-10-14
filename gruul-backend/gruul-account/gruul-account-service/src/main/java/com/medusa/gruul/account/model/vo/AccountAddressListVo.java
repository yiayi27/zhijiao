package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2023/11/19
 */
@Data
public class AccountAddressListVo {

    @Schema(title = "地址id")
    private Long id;

    @Schema(title = "收货人姓名")
    private String userName;

    @Schema(title = "收货人联系电话")
    private String phone;

    @Schema(title = "是否默认 0-非默认 1-默认")
    private Integer isDefault;

    @Schema(title = "省")
    private String province;

    @Schema(title = "市")
    private String city;

    @Schema(title = "区")
    private String county;

    @Schema(title = "详细收货地址信息")
    private String detailInfo;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "经度,维度")
    private String location;

    @Schema(title = "编码")
    private String postCode;

}
