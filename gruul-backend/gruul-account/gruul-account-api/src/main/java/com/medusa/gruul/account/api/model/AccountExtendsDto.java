package com.medusa.gruul.account.api.model;

import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.common.valid.NoSpaces;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2025/4/11
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AccountExtendsDto implements Serializable {
    /**
     * 店铺id
     */
    @NotBlank
    private String shopId;

    /**
     * 用户id
     */
    @NotBlank
    private String userId;

    /**
     * 手机号码 为空表示不更新新
     */
    @Pattern(regexp = RegexPool.MOBILE)
    private String mobile;

    /**
     * 用户昵称 为空表示不更新该字段
     */
    @NoSpaces
    private String nickname;

    /**
     * 用户头像 为空表示不更新该字段
     */
    private String avatar;

}
