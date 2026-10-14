package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author whh
 * @description
 * @date: 2024/5/23
 */
@Data
public class MotifyMsgStateDto {

    @Schema(title = "id")
    @NotNull
    private Integer id;

    /**
     * 小程序订阅消息是否开启 0-关闭  1-开启
     */
    @Schema(title = "小程序订阅消息是否开启 0-关闭  1-开启")
    @Range(min = 0, max = 1)
    private Integer miniOpen;

    /**
     * 短信模板消息是否开启 0-关闭  1-开启
     */
    @Schema(title = "短信模板消息是否开启 0-关闭  1-开启")
    @Range(min = 0, max = 1)
    private Integer codeOpen;


    /**
     * 公众号是否发送  0关闭- 1-开启
     */
    @Schema(title = "公众号是否发送  0关闭- 1-开启")
    @Range(min = 0, max = 1)
    private Integer mpOpen;

}
