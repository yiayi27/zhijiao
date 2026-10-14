package com.medusa.gruul.shops.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户中心菜单配置
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_account_center_menu")
@Schema(title = "AccountCenterMenu对象", description = "用户中心菜单配置")
public class AccountCenterMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(title = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 菜单名称
     */
    @Schema(title = "菜单名称")
    @TableField("menu_name")
    private String menuName;

    /**
     * 菜单url
     */
    @Schema(title = "菜单url")
    @TableField("menu_icon_url")
    private String menuIconUrl;


    /**
     * 菜单url
     */
    @Schema(title = "默认图标地址")
    @TableField("default_icon")
    private String defaultIcon;

    /**
     * 菜单样式类别 1-展开式 2-折叠式
     */
    @Schema(title = "菜单样式类别 1-展开式 2-折叠式")
    @TableField("style_type")
    private Integer styleType;

    /**
     * 排序位置
     */
    @Schema(title = "排序位置")
    @TableField("sort_index")
    private Integer sortIndex;

    /**
     * 父级id
     */
    @Schema(title = "父级id")
    @TableField("p_id")
    private Long pId;

    /**
     * 0隐藏1显示
     */
    @Schema(title = "0隐藏1显示")
    @TableField("hide_menu")
    private Boolean hideMenu;

    /**
     * 底部是否存在分割线
     */
    @Schema(title = "底部是否存在分割线")
    @TableField("split_flag")
    private Boolean splitFlag;

    /**
     * 是否可用 0不可用 1-可用
     */
    @Schema(title = "是否可用 0不可用 1-可用")
    @TableField("allow_use")
    private Integer allowUse;

    /**
     * 跳转类型 随组件
     */
    @Schema(title = "跳转类型 随组件,前端自行决定")
    @TableField("link_select_item")
    private String linkSelectItem;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

}
