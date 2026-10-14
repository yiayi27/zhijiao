package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2025/4/14
 */
@Getter
@Setter
@ToString
@Schema(title = "开放平台授权二维码请求参数")
public class WechatOpenCodeDTO {

    /**
     * 重定向地址
     */
    @Schema(title = "重定向地址")
    private String redirectUrl;

    /**
     * 使用场景
     */
    @Schema(title = "使用场景")
    private String scenes;
}
