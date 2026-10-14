package com.medusa.gruul.solitaire.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 小程序资讯设置查询返回信息
 * </p>
 *
 * @author lcysike
 * @since 2024-05-27
 */
@Data
@Schema(title = "ApiInformationSetVo", description = "小程序资讯设置查询返回信息")
public class ApiInformationSetVo {

    /**
     * 是否开启资讯功能,0=禁止,1=开启
     */
    @Schema(title = "是否开启资讯功能,0=禁止,1=开启")
    private Integer informationType;

    /**
     * 留言管理员名称
     */
    @Schema(title = "留言管理员名称")
    private String adminName;
}
