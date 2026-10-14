package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/4/11
 */
@Data
public class AssembleActivityAreaAssociationVo {

    private static final long serialVersionUID = -2794714737190550233L;
    @Schema(title = "头像url")
    private String avatarUrl;
    @Schema(title = "用户名称")
    private String nikeName;
}
