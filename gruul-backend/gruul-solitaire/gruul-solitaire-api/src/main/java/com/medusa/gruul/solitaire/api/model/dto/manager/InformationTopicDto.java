package com.medusa.gruul.solitaire.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.solitaire.api.entity.InformationTopic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 资讯专题传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@Schema(title = "InformationTopicDto", description = "资讯专题传输对象")
public class InformationTopicDto {

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

    public InformationTopic coverBean() {
        InformationTopic informationTopic = new InformationTopic();
        BeanUtil.copyProperties(this, informationTopic);
        return informationTopic;
    }
}
