package com.medusa.gruul.solitaire.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.solitaire.api.entity.InformationInfo;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationInfoDto;
import com.medusa.gruul.solitaire.api.model.param.manager.InformationInfoParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationInfoVo;

import java.util.List;

/**
 * <p>
 * 资讯信息 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024/05/26
 */
public interface IInformationInfoService extends IService<InformationInfo> {

    /**
     * 查询资讯信息
     *
     * @param informationInfoParam
     * @return IPage<InformationTopicVo>
     */
    IPage<InformationInfoVo> list(InformationInfoParam informationInfoParam);

    /**
     * 查询资讯信息
     *
     * @param id 资讯id
     * @return InformationInfoVo
     */
    InformationInfoVo get(Long id);

    /**
     * 资讯信息新增/修改
     *
     * @param informationInfoDto
     */
    void save(InformationInfoDto informationInfoDto);

    /**
     * 资讯信息排序
     *
     * @param informationInfoDtos
     */
    void sort(List<InformationInfoDto> informationInfoDtos);

    /**
     * 资讯信息删除
     *
     * @param ids 资讯信息ids
     */
    void delete(Long[] ids);

}
