package com.medusa.gruul.platform.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 */
@Data
public class WxOpenMaCodeTemplateVo {

    @Schema(title = "模板id")
    private Long templateId;

    @Schema(title = "模版版本号(ide上传时)")
    private String userVersion;

    @Schema(title = "模版描述(ide上传时)")
    private String userDesc;

    @Schema(title = "开发者上传草稿时间 / 被添加为模版的时间(ide上传时)")
    private String createTime;
}
