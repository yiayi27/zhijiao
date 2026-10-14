package com.medusa.gruul.platform.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/5/22
 */
@Data
public class ShopMessageDetailVo {

    @Schema(title = "id")
    private Long id;


    /**
     * 标题名称
     */
    @Schema(title = "标题名称")
    private String title;

    /**
     * 小程序订阅消息是否开启 0-关闭  1-开启
     */
    @Schema(title = "小程序订阅消息是否开启 0-关闭  1-开启")
    private Integer miniOpen;

    /**
     * 小程序订阅模板
     */
    @Schema(title = "小程序订阅模板")
    private String miniMsg;

    /**
     * 短信模板消息是否开启 0-关闭  1-开启
     */
    @Schema(title = "短信模板消息是否开启 0-关闭  1-开启")
    private Integer codeOpen;

    /**
     * 短信消息
     */
    @Schema(title = "短信消息")
    private String codeMsg;

    /**
     * 公众号是否发送  0关闭- 1-开启
     */
    @Schema(title = "公众号是否发送  0关闭- 1-开启")
    private Integer mpOpen;

    /**
     * 公众号消息
     */
    @Schema(title = "公众号消息")
    private String mpMsg;

}
