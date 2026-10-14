package com.medusa.gruul.solitaire.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.solitaire.api.entity.InformationSet;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationSetDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationSetVo;

/**
 * <p>
 * 资讯设置 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024/05/25
 */
public interface IInformationSetService extends IService<InformationSet> {

    /**
     * 查询资讯设置信息
     *
     * @return InformationSetVo
     */
    InformationSetVo get();

    /**
     * 资讯设置新增/修改
     *
     * @param informationSetDto
     */
    void save(InformationSetDto informationSetDto);

}
