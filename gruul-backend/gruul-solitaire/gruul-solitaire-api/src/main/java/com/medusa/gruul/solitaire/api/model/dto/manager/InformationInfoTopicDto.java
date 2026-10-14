package com.medusa.gruul.solitaire.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.solitaire.api.entity.InformationInfoTopic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * <p>
 * 资讯关联专题信息传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@Data
@Schema(title = "InformationInfoTopicDto", description = "资讯关联专题信息传输对象")
public class InformationInfoTopicDto {

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

    public InformationInfoTopic coverBean() {
        InformationInfoTopic informationInfoTopic = new InformationInfoTopic();
        BeanUtil.copyProperties(this, informationInfoTopic);
        return informationInfoTopic;
    }
}
