package com.medusa.gruul.solitaire.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.solitaire.api.entity.InformationMessage;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationMessageDto;
import com.medusa.gruul.solitaire.api.model.param.manager.InformationMessageParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationMessageVo;

import java.util.List;

/**
 * <p>
 * 资讯留言信息 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024/05/26
 */
public interface IInformationMessageService extends IService<InformationMessage> {

    /**
     * 查询资讯留言信息
     *
     * @param informationMessageParam
     * @return List<InformationMessageVo>
     */
    PageUtils<InformationMessageVo> list(InformationMessageParam informationMessageParam);

    /**
     * 资讯留言回复
     *
     * @param informationMessageDto
     */
    void reply(InformationMessageDto informationMessageDto);

    /**
     * 资讯信息设为/取消星标
     *
     * @param informationMessageDto
     */
    void star(InformationMessageDto informationMessageDto);

    /**
     * 资讯留言排序
     *
     * @param informationMessageDtos
     */
    void sort(List<InformationMessageDto> informationMessageDtos);

    /**
     * 资讯留言删除
     *
     * @param id 资讯信息id
     */
    void delete(Long id);

}
