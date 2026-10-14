package com.medusa.gruul.platform.model.dto.admin;

import com.medusa.gruul.common.core.constant.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2025/4/9
 */
@Getter
@Setter
@ToString
@Schema(description = "管理员信息")
public class ShopAdminAddDTO extends ShopAdminUpdateDTO {

    /**
     * 管理员密码
     */
    @Schema(description = "管理员密码")
    @Size(min = 6, max = 20, message = "管理员密码长度必须在6-20位之间")
    @Pattern(regexp = RegexConstants.PASSWORD, message = "只能是数字、大小写字母的组合，至少包含其中两种")
    private String password;

}
