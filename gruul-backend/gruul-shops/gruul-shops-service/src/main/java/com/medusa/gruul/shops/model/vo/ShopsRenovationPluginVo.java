package com.medusa.gruul.shops.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Accessors(chain = true)
@Schema(title = "ShopsRenovationPluginVo 实体", description = "店铺装修模板全局控件实体 vo")
public class ShopsRenovationPluginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 控件JSON
     */
    @Schema(title = "控件JSON")
    private String pluginProperties;

    /**
     * 备用字段
     */
    @Schema(title = "备用字段")
    private String spare;


    /**
     * 控件名称中文
     */
    @Schema(title = "控件名称中文")
    private String pluginNameCn;

    /**
     * 控件名称英文
     */
    @Schema(title = "控件名称英文")
    private String pluginNameEn;


    /**
     * 是否选中 0否 1是
     */
    @Schema(title = "是否选中 0否 1是")
    private String isSelection;


    /**
     * 是否允许取消 0否 1是
     */
    @Schema(title = "是否允许取消 0否 1是")
    private String isMandatory;


    /**
     * 默认装修
     *
     * @return ShopsRenovationPluginVo 默认值
     */
    public static ShopsRenovationPluginVo defaultPlugin() {
        return new ShopsRenovationPluginVo()
                .setPluginProperties("[{\"icon\":\"\",\"value\":\"NavBar\",\"label\":\"底部导航\",\"id\":1744264240047,\"formData\":{\"menuList\":[{\"sortIndex\":0,\"selectedIconPath\":\"https://dingwen-wxshop.oss-cn-hangzhou.aliyuncs.com/20250410/ae100dea0ce04093bd5d2f1751bf721c.png\",\"iconType\":2,\"linkUrl\":\"/pages/index/index\",\"name\":\"\",\"linkSelectItem\":\"\",\"isHome\":true,\"text\":\"首页\",\"id\":\"346\",\"iconPath\":\"https://dingwen-wxshop.oss-cn-hangzhou.aliyuncs.com/20250410/6bef95f876d647708f4eb19e711eda5f.png\",\"type\":5,\"linkName\":\"\",\"defIcon\":\"https://oss-tencent.bgniao.cn/api/home_page.png\",\"actIcon\":\"https://oss-tencent.bgniao.cn/api/home_page1.png\",\"getFrom\":\"bottomNav\"},{\"sortIndex\":1,\"selectedIconPath\":\"https://dingwen-wxshop.oss-cn-hangzhou.aliyuncs.com/20250410/f95d0ed913d345ffbefb8a672d36c3c0.png\",\"iconType\":2,\"linkUrl\":\"/pages/index/index\",\"name\":\"mall\",\"linkSelectItem\":\"\",\"isHome\":false,\"text\":\"商超\",\"id\":97,\"iconPath\":\"https://dingwen-wxshop.oss-cn-hangzhou.aliyuncs.com/20250410/a6898fb7f5a24ffa85741ad196aec356.png\",\"type\":2,\"linkName\":\"首页专区\",\"defIcon\":\"https://oss-tencent.bgniao.cn/api/shopping_mall1.png\",\"actIcon\":\"https://oss-tencent.bgniao.cn/api/shopping_mall.png\",\"getFrom\":\"bottomNav\"},{\"sortIndex\":2,\"selectedIconPath\":\"https://dingwen-wxshop.oss-cn-hangzhou.aliyuncs.com/20250410/8a3c79470b464ea386f24bdf7232f42d.png\",\"iconType\":2,\"linkUrl\":\"/pages/index/index\",\"name\":\"shopCar\",\"linkSelectItem\":\"\",\"isHome\":false,\"text\":\"购物车\",\"id\":4,\"iconPath\":\"https://dingwen-wxshop.oss-cn-hangzhou.aliyuncs.com/20250410/7720ff12fb6446238dd01f1f74217e82.png\",\"type\":0,\"linkName\":\"购物车\",\"defIcon\":\"https://oss-tencent.bgniao.cn/api/shopping_cart1.png\",\"actIcon\":\"https://oss-tencent.bgniao.cn/api/shopping_cart.png\",\"getFrom\":\"bottomNav\"},{\"sortIndex\":3,\"selectedIconPath\":\"https://dingwen-wxshop.oss-cn-hangzhou.aliyuncs.com/20250410/fbddb75e676d4540bfc9f846b294d71a.png\",\"iconType\":2,\"linkUrl\":\"/pages/index/index\",\"name\":\"me\",\"linkSelectItem\":\"\",\"isHome\":false,\"text\":\"我的\",\"id\":3,\"iconPath\":\"https://dingwen-wxshop.oss-cn-hangzhou.aliyuncs.com/20250410/0497771abe6849589447a070fae0d7f9.png\",\"type\":0,\"linkName\":\"个人中心\",\"defIcon\":\"https://oss-tencent.bgniao.cn/api/my1.png\",\"actIcon\":\"https://oss-tencent.bgniao.cn/api/my.png\",\"getFrom\":\"bottomNav\"}],\"defaultColor\":\"#084E43\",\"selectColor\":\"#084E43\",\"codeStyle\":1}}]")
                .setPluginNameCn("底部导航")
                .setPluginNameEn("navBar")
                .setIsSelection("1")
                .setIsMandatory("0");
    }

}