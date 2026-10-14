package com.medusa.gruul.shops.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2023/11/20
 */
@Data
public class AccountCenterVo {

    @Schema(title = "用户中心数据id")
    private Long id;

    @Schema(title = "店铺id")
    private String shopId;

    @Schema(title = "头部风格 1-系统风格 2-自定义风格")
    private Integer headStyle;

    @Schema(title = "自定义风格样式,json存储")
    private String customStyle;

    @Schema(title = "领卡文案")
    private String getCartText;

    @Schema(title = "非会员隐藏领卡入口 0-隐藏 1-显示")
    private Boolean hideCartInlet;

    @Schema(title = "订单icon信息 json存储")
    private String orderInfo;

    @Schema(title = "菜单选择样式 1-展开式 2-折叠式")
    private Integer menuStyle;

    @Schema(title = "菜单")
    private List<MenuVo> menuVos;
}
