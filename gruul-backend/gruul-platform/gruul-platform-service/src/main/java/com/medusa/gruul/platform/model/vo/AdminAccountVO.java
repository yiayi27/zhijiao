package com.medusa.gruul.platform.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author 张治保
 * @since 2025/4/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Schema(title = "管理员信息")
public class AdminAccountVO {

    /**
     * 管理员昵称
     */
    @Schema(title = "管理员昵称")
    private String nickname;

    /**
     * 管理员手机号
     */
    @Schema(title = "管理员手机号")
    private String mobile;

    /**
     * 管理员权限
     */
    @Schema(title = "管理员权限")
    private Set<String> perms;

    /**
     * 是否可编辑
     */
    @Schema(title = "是否可编辑、删除", description = "true： 可编辑、删除，false：不可编辑、删除")
    private Boolean editable;
}
