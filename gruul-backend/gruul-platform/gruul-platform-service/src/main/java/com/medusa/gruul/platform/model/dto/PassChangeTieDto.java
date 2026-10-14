package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author cqj
 */
@Data
@Accessors(chain = true)
public class PassChangeTieDto {

    @Schema(title = "修改的密码")
    private String passwd;

    @Schema(title = "校验码")
    private String certificate;
}
