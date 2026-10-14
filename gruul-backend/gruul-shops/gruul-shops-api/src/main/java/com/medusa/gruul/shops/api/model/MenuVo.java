package com.medusa.gruul.shops.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2023/12/14
 */
@Data
public class MenuVo {


    @Schema(title = "菜单名称")
    private Long id;

    @Schema(title = "菜单名称")
    private String menuName;

    @Schema(title = "菜单当前图标url")
    private String menuIconUrl;

    @Schema(title = "默认图标地址")
    private String defaultIcon;

    @Schema(title = "菜单样式类别 1-展开式 2-折叠式")
    private Integer styleType;

    @Schema(title = "排序位置")
    private Integer sortIndex;

    @Schema(title = "父级id")
    private Long pId;

    @Schema(title = "菜单是否 0隐藏1显示")
    private Boolean hideMenu;

    @Schema(title = "底部是否存在分割线")
    private Boolean splitFlag;


    @Schema(title = "是否可用 0不可用 1-可用")
    private Integer allowUse;

    @Schema(title = "跳转类型前端决定")
    private String linkSelectItem;

    @Schema(title = "子菜单")
    private List<MenuVo> subMenu;
}
