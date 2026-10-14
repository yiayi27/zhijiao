package com.medusa.gruul.shops.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2023/12/13
 */
@Data
public class AccountCenterMenuDto implements Serializable {

    private static final long serialVersionUID = -5305291981282924071L;

    @Schema(title = "菜单id")
    private Long menuId;

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
    private List<AccountCenterMenuDto> subMenu;
}
