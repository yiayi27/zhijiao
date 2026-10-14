package com.medusa.gruul.solitaire.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.solitaire.api.entity.InformationTopic;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationTopicVo;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 资讯专题 Mapper接口
 * </p>
 *
 * @author lcysike
 * @since 2024/05/25
 */
@Component
public interface InformationTopicMapper extends BaseMapper<InformationTopic> {

    /**
     * 获取资讯专题明细
     *
     * @return 资讯专题集合对象
     */
    List<InformationTopicVo> queryList();
}
