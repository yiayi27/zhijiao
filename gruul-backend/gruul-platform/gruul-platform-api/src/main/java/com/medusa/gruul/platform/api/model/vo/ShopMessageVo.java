package com.medusa.gruul.platform.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2024/5/22
 */
@Data
public class ShopMessageVo {

    /**
     * 消息标题
     */
    @Schema(title = "消息标题")
    private String msgTitle;

    /**
     * 模板消息id
     */
    @Schema(title = "模板消息id")
    private List<ShopMessageDetailVo> shopMessageDetailVos;
}
