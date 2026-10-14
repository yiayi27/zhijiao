package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2023/11/20
 */
@Data
public class TagDto {

    @Schema(title = "标签名")
    private String tagName;

    @Schema(title = "标签id")
    private Long tagId;

    @Schema(title = "是否选中 false 或 trun(选中) ")
    private Boolean option;

}
