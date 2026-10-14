package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2023/11/27
 */
@Data
public class UpdateUserBaseInfoDto {

    @Schema(title = "昵称")
    private String nikeName;
    @Schema(title = "性别")
    private int gender;
    @Schema(title = "语言")
    private String language;
    @Schema(title = "城市")
    private String city;
    @Schema(title = "省")
    private String province;
    @Schema(title = "国家")
    private String country;
    @Schema(title = "头像")
    private String avatarUrl;
}
