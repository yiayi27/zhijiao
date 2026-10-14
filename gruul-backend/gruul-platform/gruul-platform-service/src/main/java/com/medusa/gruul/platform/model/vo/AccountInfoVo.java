package com.medusa.gruul.platform.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author whh
 * @description
 * @date: 2024/3/7
 */
@Data
@Accessors(chain = true)
public class AccountInfoVo implements Serializable {
    private static final long serialVersionUID = 5740795642506439829L;


    @Schema(title = "用户名称")
    private String nikeName;

    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "手机号码")
    private String phone;

}
