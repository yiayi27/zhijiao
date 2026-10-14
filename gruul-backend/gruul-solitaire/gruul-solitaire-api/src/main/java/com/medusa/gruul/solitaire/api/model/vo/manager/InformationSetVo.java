package com.medusa.gruul.solitaire.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 资讯设置查询返回信息
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@Schema(title = "InformationSetVo", description = "资讯设置查询返回信息")
public class InformationSetVo {

    private Long id;

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
