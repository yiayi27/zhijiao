package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2023/11/19
 */
@Data
@Schema(title = "用户标签")
public class UserTagVo {

    @Schema(title = "标签id")
    private Long tagId;


    @Schema(title = "标签名")
    private String tagName;
}
