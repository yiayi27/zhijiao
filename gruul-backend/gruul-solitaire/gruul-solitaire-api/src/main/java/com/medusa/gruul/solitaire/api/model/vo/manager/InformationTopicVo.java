package com.medusa.gruul.solitaire.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 资讯专题查询返回信息
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@Schema(title = "InformationTopicVo", description = "资讯专题查询返回信息")
public class InformationTopicVo {

    private Long id;

    /**
     * 专题名称
     */
    @Schema(title = "专题名称")
    private String topicName;

    /**
     * 专题图标
     */
    @Schema(title = "专题图标")
    private String topicLogo;

    /**
     * 排序
     */
    @Schema(title = "排序")
    private String sort;

    /**
     * 专题下资讯条数
     */
    @Schema(title = "专题下资讯条数")
    private Integer infoCount = 0;
}
