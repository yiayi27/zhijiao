package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description 小程序端购买会员卡填写信息
 * @Author zhaokw
 * @Date 10:59 2024\6\7 0007
 **/
@Data
@Schema(title = "小程序端购买会员卡填写信息")
@Accessors(chain = true)
public class RegisterDataVo {

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "店铺ID")
    private String shopId;
    @Schema(title = "手机号")
    @NotBlank(message = "参数为空")
    private String phone;
    @Schema(title = "验证码")
    @NotBlank(message = "参数为空")
    private String code;
    @Schema(title = "扩展表单数据")
    @NotNull(message = "缺少必要数据")
    private @Valid List<FormExdDto> formExdDto;

    @Setter
    @Getter
    public static class FormExdDto {

        @Schema(title = "会员注册表单id")
        @NotNull(message = "数据格式异常")
        private Long id;

        @Schema(title = "表单id")
        @NotBlank(message = "数据格式异常")
        private String formId;

        @Schema(title = "填写的表单值")
        private String value;

        @Schema(title = "填写的表单名称")
        private String formName;
    }

}
