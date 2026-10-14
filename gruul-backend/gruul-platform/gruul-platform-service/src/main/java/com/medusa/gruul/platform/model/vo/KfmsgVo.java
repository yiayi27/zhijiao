package com.medusa.gruul.platform.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/9/28
 */
@Data
public class KfmsgVo {

    @Schema(title = "客服消息管理员openId")
    private String kfOpenIds;
    @Schema(title = "客服消息模板id")
    private String kfmsgTemplateId;
    @Schema(title = "开票消息管理员openId")
    private String invoiceOpenIds;
    @Schema(title = "开票消息模板id")
    private String invoiceTemplateId;
}
