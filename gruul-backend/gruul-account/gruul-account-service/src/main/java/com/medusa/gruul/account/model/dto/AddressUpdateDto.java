package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2023/11/19
 */
@Data
public class AddressUpdateDto {

    @Schema(title = "地址id")
    private Long id;

    @Schema(title = "省")
    @NotBlank(message = "省为空")
    private String province;

    @Schema(title = "市")
    @NotBlank(message = "市为空")
    private String city;

    @Schema(title = "区")
    @NotBlank(message = "区为空")
    private String county;

    @Schema(title = "收货人电话")
    @NotBlank(message = "收货人电话为空")
    private String phone;

    @Schema(title = "详细地址")
    @NotBlank(message = "详细地址为空")
    private String detailInfo;

    @Schema(title = "提货人姓名")
    @NotBlank(message = "提货人姓名为空")
    private String userName;

    @Schema(title = "经度,维度")
    @NotBlank(message = "经度,维度为空")
    private String location;

    @Schema(title = "是否设置为默认地址,是否默认 0-非默认 1-默认")
    private Integer defaultStatus;

    @Schema(title = "编码")
    private String postCode;
}
