package com.medusa.gruul.platform.model.dto;

import com.medusa.gruul.common.core.constant.RegexConstants;
import com.medusa.gruul.common.valid.NoSpaces;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2025/4/10
 */
@Getter
@Setter
@ToString
@Schema(title = "更新用户资料模型定义", description = "用于用户更新昵称、头像")
public class AccountDataUpdateDTO implements Serializable {

    /**
     * 昵称
     */
    @Schema(title = "用户昵称")
    @NotBlank
    @Size(min = 1, max = 15)
    private String nickname;

    /**
     * 头像
     */
    @Schema(title = "用户头像")
    @NotBlank
    @NoSpaces
    private String avatar;
}
