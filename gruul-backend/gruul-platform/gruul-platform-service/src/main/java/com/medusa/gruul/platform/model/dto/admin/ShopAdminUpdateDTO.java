package com.medusa.gruul.platform.model.dto.admin;

import com.medusa.gruul.common.core.constant.RegexConstants;
import com.medusa.gruul.common.valid.NoSpaces;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * @since 2025/4/10
 */
@Getter
@Setter
@ToString
public class ShopAdminUpdateDTO implements Serializable {

    /**
     * 管理员手机号
     */
    @Schema(description = "管理员手机号")
    @NotBlank(message = "管理员手机号不能为空")
    @Pattern(regexp = RegexConstants.MOBILE, message = "管理员手机号格式不正确")
    private String mobile;

    /**
     * 管理员昵称
     */
    @Schema(description = "管理员昵称")
    @NotBlank(message = "管理员昵称不能为空")
    @Size(max = 16, message = "管理员昵称长度不能超过16位")
    @NoSpaces(message = "管理员昵称不能包含空格")
    private String nickname;

    /**
     * 管理员权限
     */
    @Schema(description = "管理员权限")
    @NotNull(message = "管理员权限不能为空")
    @Size(min = 1, message = "管理员权限至少需要一个")
    private Set<@NoSpaces String> perms;
}
