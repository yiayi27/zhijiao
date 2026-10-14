package com.medusa.gruul.solitaire.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.solitaire.api.entity.InformationTopic;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationTopicDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationTopicVo;

import java.util.List;

/**
 * <p>
 * 资讯专题 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024/05/25
 */
public interface IInformationTopicService extends IService<InformationTopic> {

    /**
     * 查询资讯专题信息
     *
     * @return List<InformationTopicVo>
     */
    List<InformationTopicVo> get();

    /**
     * 资讯专题新增/修改(支持批量)
     *
     * @param informationTopicDtos
     */
    void save(List<InformationTopicDto> informationTopicDtos);

    /**
     * 资讯专题排序
     *
     * @param informationTopicDtos
     */
    void sort(List<InformationTopicDto> informationTopicDtos);

    /**
     * 资讯专题删除
     *
     * @param id 资讯专题id
     */
    void delete(Long id);

}
