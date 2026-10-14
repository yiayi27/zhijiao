package com.medusa.gruul.platform.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/4/18
 */
@Data
public class ShopInfoVo {

    @Schema(title = "管理台店铺的shopId,表id")
    private String platformShopId;

    @Schema(title = "店铺名称")
    private String shopName;

    @Schema(title = "店铺log")
    private String logoUrl;


    @Schema(title = "营业时间,自行分割")
    private String businessHours;

    @Schema(title = "小程序底部打标")
    private String miniBottomLog;

    @Schema(title = "店铺电话")
    private String shopPhone;

    @Schema(title = "1部署中 2正常 ，3已打烊，4禁用,5不在营业时间 6店铺到期")
    private Integer status;

    @Schema(title = "appid", description = "appid（小程序或其它）主要用于微信支付，和小程序登录")
    private String appid;

    @Schema(title = "appSecret", description = "小程序密钥 用于小程序登录")
    private String appSecret;
}
