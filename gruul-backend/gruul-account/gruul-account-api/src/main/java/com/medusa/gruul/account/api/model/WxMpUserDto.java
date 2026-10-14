package com.medusa.gruul.account.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/7/21
 */
@Data
public class WxMpUserDto {

    @Schema(title = "公众号用户openId")
    private String openId;

    @Schema(title = "关联unionId")
    private String unionId;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "性别描述信息：男、女、未知等.")
    private String sexDesc;

    @Schema(title = "性别表示：1，2等数字.")
    private Integer sex;

    @Schema(title = "语言")
    private String language;

    @Schema(title = "城市")
    private String city;

    @Schema(title = "省份")
    private String province;

    @Schema(title = "国家")
    private String country;

    @Schema(title = "头像")
    private String headImgUrl;

    @Schema(title = "店铺id")
    private String shopId;
}
