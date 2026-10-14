package com.medusa.gruul.solitaire.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * <p>
 * 小程序资讯关联专题信息查询返回信息
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@Data
@Schema(title = "ApiInformationInfoTopicVo", description = "小程序资讯关联专题信息查询返回信息")
public class ApiInformationInfoTopicVo {

    private Long id;

    /**
     * 资讯id
     */
    @Schema(title = "资讯id")
    private Long infoId;

    /**
     * 专题id
     */
    @Schema(title = "专题id")
    private Long topicId;

    /**
     * 专题名称
     */
    @Schema(title = "专题名称")
    private String topicName;
}
