package com.medusa.gruul.platform.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cqj
 */
@Data
public class MiniMsgVo {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "消息标识,唯一值,auditMsg-审核通知  sendMsg-发货通知")
    private String mark;

    @Schema(title = "模板消息id")
    private String miniTemplateId;

    @Schema(title = "开启状态 0-未使用  1-使用")
    private Integer miniOpen;

    /**
     * 小程序订阅模板
     */
    @Schema(title = "小程序订阅模板")
    private String miniMsg;

}
