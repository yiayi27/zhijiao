package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description 会员注册表单集合
 * @Author zhaokw
 * @Date 21:19 2024\5\15 0015
 **/
@Data
@Schema(title = "会员注册表单集合")
@Accessors(chain = true)
public class MemberRegisterFormVo {

    @Schema(title = "会员注册表单id")
    private Long id;

    @Schema(title = "表单id")
    private String formId;

    @Schema(title = "表单名称")
    private String formName;

    @Schema(title = "表单格式 1表示身份证，2表示文本  3-手机号")
    private Integer formPattern;

    @Schema(title = "是否必填，1表示必填，2表示非必填")
    private Integer isRequired;

    @Schema(title = "提示语")
    private String tips;

}
