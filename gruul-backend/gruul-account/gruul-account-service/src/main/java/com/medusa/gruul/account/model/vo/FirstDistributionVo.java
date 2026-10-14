package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "一级分销")
public class FirstDistributionVo {

    @Schema(title = "所属分销员名称")
    private String firstNickName;

    @Schema(title = "所属分销员手机")
    private String firstPhone;
}
