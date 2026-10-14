package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/4/11
 */
@Data
public class AssembleActivityAssociationVo {
    private static final long serialVersionUID = 4331728369625094037L;
    @Schema(title = "头像url")
    private String avatarUrl;
    @Schema(title = "用户名称")
    private String nikeName;
}
