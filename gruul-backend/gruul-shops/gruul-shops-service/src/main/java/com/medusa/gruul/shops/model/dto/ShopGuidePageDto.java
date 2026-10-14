package com.medusa.gruul.shops.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.shops.api.entity.ShopGuidePage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 商家自定义引导页dto
 * @Author: xiaoq
 * @Date: 2024/10/16 9:57
 */
@Data
@Schema(title = "商家自定义引导页dto")
public class ShopGuidePageDto {

    @Schema(title = "id")
    private Long id;
    @Schema(title = "网址")
    private String url;

    @Schema(title = "路径")
    private String path;

    @Schema(title = "path名称 用于比较")
    private String link;

    @Schema(title = "跳转的小程序id")
    private String appId;

    @Schema(title = " 0为path 1为app_id")
    private Integer type;


    public ShopGuidePage coverShopGuidePage() {
        ShopGuidePage shopGuidePage = new ShopGuidePage();
        BeanUtil.copyProperties(this, shopGuidePage);
        return shopGuidePage;
    }

}
