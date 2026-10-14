package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cqj
 */
@Data
public class PhoneChangeTieDto {

    @Schema(title = "换绑后手机号")
    private String newPhone;


    @Schema(title = "1级校验码凭证")
    private String oneCertificate;

    @Schema(title = "二级校验码凭证")
    private String twoCertificate;
}
